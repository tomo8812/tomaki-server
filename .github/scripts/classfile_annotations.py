"""Small, dependency-free reader for JVM declaration annotations (no class loading)."""
import io
import struct


class Reader:
    def __init__(self, data):
        self.stream = io.BytesIO(data)

    def read(self, size):
        data = self.stream.read(size)
        if len(data) != size:
            raise ValueError('truncated classfile')
        return data

    def u1(self):
        return self.read(1)[0]

    def u2(self):
        return struct.unpack('>H', self.read(2))[0]

    def u4(self):
        return struct.unpack('>I', self.read(4))[0]


def parse_class(data):
    r = Reader(data)
    if r.u4() != 0xCAFEBABE:
        raise ValueError('invalid classfile magic')
    r.u2()
    major = r.u2()
    cp = [None] * r.u2()
    i = 1
    while i < len(cp):
        tag = r.u1()
        if tag == 1:
            # JVM modified UTF-8: identifiers/descriptors are ASCII; preserve
            # arbitrary string constants without letting them affect parsing.
            cp[i] = r.read(r.u2()).replace(b'\xc0\x80', b'\0').decode('utf-8', 'surrogatepass')
        elif tag in (3, 4):
            cp[i] = struct.unpack('>i' if tag == 3 else '>f', r.read(4))[0]
        elif tag in (5, 6):
            cp[i] = r.read(8)
            i += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[i] = (tag, r.u2())
        elif tag in (9, 10, 11, 12, 17, 18):
            cp[i] = (tag, r.u2(), r.u2())
        elif tag == 15:
            cp[i] = (tag, r.u1(), r.u2())
        else:
            raise ValueError(f'unknown constant pool tag {tag}')
        i += 1

    def value(a):
        tag = chr(a.u1())
        if tag in 'BCDFIJSZs':
            return {'kind': tag, 'value': cp[a.u2()]}
        if tag == 'e':
            return {'kind': tag, 'type': cp[a.u2()], 'value': cp[a.u2()]}
        if tag == 'c':
            return {'kind': tag, 'value': cp[a.u2()]}
        if tag == '@':
            return {'kind': tag, 'value': annotation(a)}
        if tag == '[':
            return {'kind': tag, 'value': [value(a) for _ in range(a.u2())]}
        raise ValueError(f'unknown annotation value tag {tag}')

    def annotation(a):
        desc = cp[a.u2()]
        values = {}
        for _ in range(a.u2()):
            name = cp[a.u2()]
            values[name] = value(a)
        return {'descriptor': desc, 'values': values}

    def attributes():
        annotations = []
        for _ in range(r.u2()):
            name, raw = cp[r.u2()], r.read(r.u4())
            if name in ('RuntimeVisibleAnnotations', 'RuntimeInvisibleAnnotations'):
                a = Reader(raw)
                for _ in range(a.u2()):
                    item = annotation(a)
                    item['visible'] = name == 'RuntimeVisibleAnnotations'
                    annotations.append(item)
        return annotations

    access, this_class, super_class = r.u2(), r.u2(), r.u2()
    interfaces = [cp[cp[r.u2()][1]] for _ in range(r.u2())]
    def members():
        result = []
        for _ in range(r.u2()):
            flags, name, desc = r.u2(), cp[r.u2()], cp[r.u2()]
            result.append({'access': flags, 'name': name, 'descriptor': desc,
                           'annotations': attributes()})
        return result
    fields, methods = members(), members()
    return {'name': cp[cp[this_class][1]], 'major': major, 'access': access,
            'super': cp[cp[super_class][1]] if super_class else None,
            'interfaces': interfaces, 'fields': fields, 'methods': methods,
            'annotations': attributes()}


def walk_annotations(annotations):
    """Yield nested @At/@Slice too, retaining the containing attribute visibility."""
    for annotation in annotations:
        yield annotation
        def nested(value):
            if value['kind'] == '@':
                child = dict(value['value'], visible=annotation['visible'])
                yield from walk_annotations([child])
            elif value['kind'] == '[':
                for child in value['value']:
                    yield from nested(child)
        for value in annotation['values'].values():
            yield from nested(value)
