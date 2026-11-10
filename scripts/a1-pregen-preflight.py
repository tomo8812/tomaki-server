#!/usr/bin/env python3
"""Quick non-destructive Oracle A1 pregen preflight. Does NOT start Minecraft.

Usage: python3 scripts/a1-pregen-preflight.py --dir /path/to/minecraft/server
Writes only into a temporary directory underneath --dir, then removes it.
"""
import argparse
import concurrent.futures
import json
import os
import platform
import shutil
import statistics
import tempfile
import time
import zlib

MIB = 1024 * 1024


def read_text(path):
    try:
        with open(path, encoding="utf-8") as f:
            return f.read().strip()
    except (OSError, UnicodeError):
        return None


def meminfo_kib(name):
    for line in (read_text('/proc/meminfo') or '').splitlines():
        if line.startswith(name + ':'):
            return int(line.split()[1])
    return None


def pressure(kind):
    data = read_text('/proc/pressure/' + kind) or ''
    for line in data.splitlines():
        if line.startswith('some '):
            try:
                return float(next(x[6:] for x in line.split() if x.startswith('avg10=')))
            except (StopIteration, ValueError):
                pass
    return None


def cpu_sample():
    line = (read_text('/proc/stat') or '').splitlines()[0].split()
    v = list(map(int, line[1:]))
    return sum(v), v[3] + v[4]


def benchmark_cpu():
    # Synthetic compression, NOT a Minecraft worldgen or NBT throughput measurement.
    data = (os.urandom(32 * 1024) + b'\0' * (32 * 1024)) * 128  # 8 MiB

    def task():
        for _ in range(2):
            zlib.compress(data, 6)

    t0 = time.perf_counter()
    task()
    single = time.perf_counter() - t0
    t0 = time.perf_counter()
    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        list(pool.map(lambda _: task(), (0, 1)))
    dual = time.perf_counter() - t0
    return {
        'synthetic_zlib_1_worker_mib_s': round(16 / max(single, 1e-9), 1),
        'synthetic_zlib_2_workers_total_mib_s': round(32 / max(dual, 1e-9), 1),
    }


def benchmark_disk(workdir):
    with tempfile.TemporaryDirectory(prefix='.pregen-preflight-', dir=workdir) as temp:
        path = os.path.join(temp, 'temp.bin')
        block = os.urandom(MIB)
        fd = os.open(path, os.O_CREAT | os.O_EXCL | os.O_WRONLY, 0o600)
        try:
            t0 = time.perf_counter()
            for _ in range(64):
                view = memoryview(block)
                while view:
                    view = view[os.write(fd, view):]
            os.fsync(fd)
            large = time.perf_counter() - t0
            small = []
            for _ in range(16):
                t0 = time.perf_counter()
                os.write(fd, block[:4096])
                os.fsync(fd)
                small.append((time.perf_counter() - t0) * 1000)
        finally:
            os.close(fd)
    return {
        'sync_write_64mib_mib_s': round(64 / max(large, 1e-9), 1),
        'sync_4k_p50_ms': round(statistics.median(small), 2),
        'sync_4k_p95_ms': round(sorted(small)[-2], 2),
        'bytes_written_for_benchmark': 64 * MIB + 16 * 4096,
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--dir', required=True, help='Directory on same filesystem as the world/cache, with >=2 GiB free')
    parser.add_argument('--output', help='Optional JSON output file (default: print only)')
    args = parser.parse_args()
    workdir = os.path.realpath(args.dir)
    if not os.path.isdir(workdir):
        parser.error('--dir must be an existing directory')
    du = shutil.disk_usage(workdir)
    if du.free < 2 * 1024**3:
        parser.error('Refusing disk test: less than 2 GiB free')
    report = {
        'profile_version': 1,
        'target': 'Oracle VM.Standard.A1.Flex 2 OCPU / 12 GB (expected; verify below)',
        'architecture': platform.machine(),
        'logical_cpus_visible': os.cpu_count(),
        'affinity_cpu_count': len(os.sched_getaffinity(0)) if hasattr(os, 'sched_getaffinity') else None,
        'cgroup_cpu_max': read_text('/sys/fs/cgroup/cpu.max'),
        'cgroup_memory_max_bytes': read_text('/sys/fs/cgroup/memory.max'),
        'mem_total_mib': round((meminfo_kib('MemTotal') or 0) / 1024),
        'mem_available_mib': round((meminfo_kib('MemAvailable') or 0) / 1024),
        'disk_free_gib': round(du.free / 1024**3, 2),
        'cpu_psi_avg10_pct': pressure('cpu'),
        'memory_psi_avg10_pct': pressure('memory'),
        'io_psi_avg10_pct': pressure('io'),
    }
    total0, idle0 = cpu_sample()
    time.sleep(1)
    total1, idle1 = cpu_sample()
    dt = total1 - total0
    report['system_cpu_utilization_1s_pct'] = round(100 * (1 - (idle1-idle0)/dt), 1) if dt > 0 else None
    report['cpu_microbenchmark'] = benchmark_cpu()
    report['disk_microbenchmark'] = benchmark_disk(workdir)
    report['warning'] = 'Synthetic CPU and sync I/O tests only; no Minecraft goodput measured. Calibrate online.'
    content = json.dumps(report, ensure_ascii=False, indent=2) + '\n'
    if args.output:
        with open(args.output, 'w', encoding='utf-8') as f:
            f.write(content)
    print(content, end='')


if __name__ == '__main__':
    main()
