package dev.diorama.render.gpu;

import dev.diorama.Diorama;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

final class GlBakeBackend implements BakeBackend {
   private static final String[] ATTRIBUTES = new String[]{"Corner", "Cell", "Color", "Occlusion", "FaceLight", "SkyIndex", "Box", "Haze"};
   private static final int UBO_BINDING = 30;
   private static final int UNITS = 6;
   private static final Pattern LOCATION = Pattern.compile("layout\\(location = \\w+\\) ?");
   private static final Pattern OUTPUT = Pattern.compile("layout\\(location = (\\d+)\\) out \\w+ (\\w+);");
   private @Nullable Boolean usable;
   private int skyProgram;
   private int shadowProgram;
   private int opaqueProgram;
   private int tintedProgram;
   private int waterProgram;
   private int compositeProgram;
   private int shadowTexture;
   private int sceneColor;
   private int sceneAux;
   private int sceneDepth;
   private int tint;
   private int waterColor;
   private int waterDepth;
   private int sky;
   private int voxels;
   private int output;
   private int outputInfo;
   private int skyFbo;
   private int shadowFbo;
   private int sceneFbo;
   private int tintFbo;
   private int waterFbo;
   private int outputFbo;
   private int faceVao;
   private int casterVao;
   private int emptyVao;
   private int cornerVbo;
   private int indexBuffer;
   private int faceVbo;
   private int casterVbo;
   private int ubo;
   private boolean timestamps;
   private @Nullable String device;
   private static boolean coreDivisor;
   private static boolean samplerObjects;
   private final ArrayDeque<GlBakeBackend.Readback> readbacks = new ArrayDeque<>();
   private static final Pattern BLOCK_COMMENT = Pattern.compile("/\\*.*?\\*/", 32);
   private static final Pattern INCLUDE = Pattern.compile("(?m)^#(include|moj_import) <diorama:bake\\.glsl>$");

   @Override
   public boolean draw(GpuTileBaker.Job job) {
      if (!this.ready()) {
         return false;
      }

      long start = System.nanoTime();
      GlBakeBackend.Saved saved = GlBakeBackend.Saved.take();
      int startQuery = 0;
      int colors = 0;
      int info = 0;
      int endQuery = 0;
      boolean queued = false;

      try {
         startQuery = this.timestamps ? GL33C.glGenQueries() : 0;
         if (startQuery != 0) {
            GL33C.glQueryCounter(startQuery, 36392);
         }

         GlState.noCullNoScissor();
         GlState.colorMaskAll();
         GlState.blend(false);
         GL33C.glBindBuffer(35052, 0);
         GL33C.glBindBuffer(35051, 0);
         GL33C.glBindBuffer(35345, this.ubo);
         GL33C.glBufferData(35345, job.uniforms, 35040);
         GL33C.glBindBufferBase(35345, 30, this.ubo);
         GL33C.glBindBuffer(34962, this.faceVbo);
         GL33C.glBufferData(34962, job.faces, 35040);
         GL33C.glBindBuffer(34962, this.casterVbo);
         GL33C.glBufferData(34962, job.casters, 35040);
         GlState.forgetBuffers();
         int skyFaces = job.opaqueCount + job.waterCount;
         if (job.voxels != null && skyFaces > 0) {
            this.bindTextures(this.voxels);
            GlBakeBackend.Saved.plainPixelStore();
            GL33C.glTexSubImage2D(3553, 0, 0, 0, 4096, job.voxelRows, 36244, 5125, job.voxels);
            this.target(this.skyFbo, 2048, 1024);
            GlState.depthTest(false);
            GlState.useProgram(this.skyProgram);
            GlState.bindVertexArray(this.faceVao);
            if (job.opaqueCount > 0) {
               this.pointFaces(0);
               GL33C.glDrawArraysInstanced(0, 0, 4, job.opaqueCount);
            }

            if (job.waterCount > 0) {
               this.pointFaces(job.opaqueCount + job.tintedCount);
               GL33C.glDrawArraysInstanced(0, 0, 4, job.waterCount);
            }
         }

         this.target(this.shadowFbo, 2048, 2048);
         this.clearDepth();
         if (job.casterCount > 0) {
            this.depth(true);
            GlState.useProgram(this.shadowProgram);
            GlState.bindVertexArray(this.casterVao);
            GL33C.glDrawElementsInstanced(4, 6, 5123, 0L, job.casterCount);
         }

         this.target(this.sceneFbo, 1028, 1028);
         this.clearColor(0, 0.01F, 0.01F, 0.015F, 1.0F);
         this.clearColor(1, 0.0F, 0.0F, 0.0F, 0.0F);
         this.clearDepth();
         this.bindTextures(this.shadowTexture, this.sky, this.voxels);
         GlState.bindVertexArray(this.faceVao);
         if (job.opaqueCount > 0) {
            this.depth(true);
            GlState.useProgram(this.opaqueProgram);
            this.pointFaces(0);
            GL33C.glDrawElementsInstanced(4, 6, 5123, 0L, job.opaqueCount);
         }

         this.target(this.tintFbo, 1028, 1028);
         this.clearColor(0, 0.0F, 0.0F, 0.0F, 1.0F);
         if (job.tintedCount > 0) {
            this.depth(false);
            GlState.blend(true);
            GlState.blendFuncSeparate(772, 1, 0, 771);
            GlState.useProgram(this.tintedProgram);
            this.pointFaces(job.opaqueCount);
            GL33C.glDrawElementsInstanced(4, 6, 5123, 0L, job.tintedCount);
            GlState.blend(false);
         }

         this.target(this.waterFbo, 1028, 1028);
         this.clearColor(0, 0.0F, 0.0F, 0.0F, 0.0F);
         this.clearDepth();
         if (job.waterCount > 0) {
            this.depth(true);
            GlState.useProgram(this.waterProgram);
            this.pointFaces(job.opaqueCount + job.tintedCount);
            GL33C.glDrawElementsInstanced(4, 6, 5123, 0L, job.waterCount);
         }

         int size = 512;
         this.target(this.outputFbo, size, size);
         GlState.depthTest(false);
         GlState.useProgram(this.compositeProgram);
         this.bindTextures(this.sceneColor, this.sceneDepth, this.sceneAux, this.tint, this.waterColor, this.waterDepth);
         GlState.bindVertexArray(this.emptyVao);
         GL33C.glDrawArrays(4, 0, 3);
         GlState.bindFramebuffer(36008, this.outputFbo);
         colors = GL33C.glGenBuffers();
         GL33C.glBindBuffer(35051, colors);
         GL33C.glBufferData(35051, (long)size * size * 4L, 35041);
         GlBakeBackend.Saved.plainPixelStore();
         GL33C.glReadBuffer(36064);
         GL33C.glReadPixels(0, 0, size, size, 6408, 5121, 0L);
         if (job.mesh.overlays != null || !job.mesh.lit) {
            info = GL33C.glGenBuffers();
            GL33C.glBindBuffer(35051, info);
            GL33C.glBufferData(35051, (long)size * size * 2L, 35041);
            GL33C.glPixelStorei(3333, 1);
            GL33C.glReadBuffer(36065);
            GL33C.glReadPixels(0, 0, size, size, 33319, 5121, 0L);
            GL33C.glReadBuffer(36064);
         }

         if (startQuery != 0) {
            endQuery = GL33C.glGenQueries();
            GL33C.glQueryCounter(endQuery, 36392);
         }

         long fence = GL33C.glFenceSync(37143, 0);
         long recordNanos = System.nanoTime() - start;
         this.readbacks.add(new GlBakeBackend.Readback(job, colors, info, fence, startQuery, endQuery, recordNanos));
         queued = true;
         job.pending();
         GpuTileBaker.recorded(recordNanos);
      } finally {
         if (!queued) {
            for (int buffer : new int[]{colors, info}) {
               if (buffer != 0) {
                  GL33C.glDeleteBuffers(buffer);
               }
            }

            for (int query : new int[]{startQuery, endQuery}) {
               if (query != 0) {
                  GL33C.glDeleteQueries(query);
               }
            }
         }

         saved.restore();
         job.free();
      }

      return true;
   }

   @Override
   public @Nullable String device() {
      if (this.device == null) {
         this.device = GL33C.glGetString(7937) + " (" + GL33C.glGetString(7936) + ", OpenGL " + GL33C.glGetString(7938) + ")";
      }

      return this.device;
   }

   @Override
   public void poll() {
      if (!this.readbacks.isEmpty()) {
         int pack = GL33C.glGetInteger(35053);

         try {
            while (!this.readbacks.isEmpty()) {
               GlBakeBackend.Readback r = this.readbacks.peek();
               int status = GL33C.glClientWaitSync(r.fence, 0, 0L);
               if (status == 37147) {
                  return;
               }

               this.readbacks.poll();
               GL33C.glDeleteSync(r.fence);
               if (status == 37149) {
                  this.free(r);
                  r.job.fail();
               } else {
                  try {
                     int size = 512;
                     int[] pixels = new int[size * size];
                     GL33C.glBindBuffer(35051, r.colors);
                     ByteBuffer mapped = GL33C.glMapBufferRange(35051, 0L, (long)size * size * 4L, 1);
                     if (mapped == null) {
                        throw new IllegalStateException("couldn't map the tile's pixels");
                     }

                     mapped.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(pixels);
                     GL33C.glUnmapBuffer(35051);
                     byte[] info = null;
                     byte[] light = null;
                     if (r.info != 0) {
                        GL33C.glBindBuffer(35051, r.info);
                        ByteBuffer bits = GL33C.glMapBufferRange(35051, 0L, (long)size * size * 2L, 1);
                        if (bits == null) {
                           throw new IllegalStateException("couldn't map the tile's overlay bits");
                        }

                        byte[] both = new byte[size * size * 2];
                        bits.get(both);
                        GL33C.glUnmapBuffer(35051);
                        info = r.job.mesh.overlays != null ? GpuTileBaker.channel(both, 0) : null;
                        light = !r.job.mesh.lit ? GpuTileBaker.channel(both, 1) : null;
                     }

                     long gpu = -1L;
                     if (r.startQuery != 0 && GL33C.glGetQueryObjecti(r.endQuery, 34919) != 0) {
                        gpu = GL33C.glGetQueryObjecti64(r.endQuery, 34918) - GL33C.glGetQueryObjecti64(r.startQuery, 34918);
                     }

                     r.job.done(pixels, info, light, r.recordNanos, gpu);
                  } catch (Throwable t) {
                     Diorama.LOGGER.error("Reading back GPU tile {} failed", r.job.mesh.key, t);
                     r.job.fail();
                  } finally {
                     this.free(r);
                  }
               }
            }
         } finally {
            GL33C.glBindBuffer(35051, pack);
         }
      }
   }

   private void free(GlBakeBackend.Readback r) {
      GL33C.glDeleteBuffers(r.colors);
      if (r.info != 0) {
         GL33C.glDeleteBuffers(r.info);
      }

      if (r.startQuery != 0) {
         GL33C.glDeleteQueries(r.startQuery);
         GL33C.glDeleteQueries(r.endQuery);
      }
   }

   private void pointFaces(int first) {
      long offset = first * 48L;
      GL33C.glBindBuffer(34962, this.faceVbo);
      GL33C.glVertexAttribIPointer(1, 4, 5122, 48, offset);
      GL33C.glVertexAttribPointer(2, 4, 5121, true, 48, offset + 8L);
      GL33C.glVertexAttribPointer(3, 4, 5121, true, 48, offset + 12L);
      GL33C.glVertexAttribPointer(4, 4, 5121, true, 48, offset + 16L);
      GL33C.glVertexAttribIPointer(5, 1, 5124, 48, offset + 20L);
      GL33C.glVertexAttribIPointer(6, 2, 5124, 48, offset + 24L);
      GL33C.glVertexAttribIPointer(7, 4, 5124, 48, offset + 32L);
   }

   private void target(int fbo, int width, int height) {
      GlState.bindFramebuffer(36009, fbo);
      GlState.viewport(width, height);
   }

   private void depth(boolean write) {
      GlState.depthTest(true);
      GlState.depthFunc(518);
      GlState.depthMask(write);
   }

   private void clearDepth() {
      GlState.depthMask(true);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         GL33C.glClearBufferfv(6145, 0, stack.floats(0.0F));
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void clearColor(int attachment, float r, float g, float b, float a) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         GL33C.glClearBufferfv(6144, attachment, stack.floats(r, g, b, a));
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void bindTextures(int... textures) {
      for (int i = 0; i < textures.length; i++) {
         GlState.activeTexture(i);
         GlState.bindTexture(textures[i]);
         noSampler(i);
      }
   }

   private boolean ready() {
      if (this.usable == null) {
         this.usable = false;

         try {
            GLCapabilities caps = GL.getCapabilities();
            coreDivisor = caps.glVertexAttribDivisor != 0L;
            if (!coreDivisor && !caps.GL_ARB_instanced_arrays) {
               Diorama.LOGGER.warn("GPU baking needs instanced vertex attributes (OpenGL 3.3); tracing tiles instead");
               return false;
            }

            samplerObjects = caps.glBindSampler != 0L;
            this.timestamps = caps.glQueryCounter != 0L && caps.glGetQueryObjecti64v != 0L;
            Diorama.LOGGER.info("GPU baking on {}", this.device());

            int error;
            for (int i = 0; i < 16 && (error = GL33C.glGetError()) != 0; i++) {
               Diorama.LOGGER.debug("OpenGL error {} was flagged before GPU baking set up; not counting it", error);
            }

            GlBakeBackend.Saved saved = GlBakeBackend.Saved.take();

            try {
               this.create(caps);
            } finally {
               saved.restore();
            }

            this.usable = true;
         } catch (Throwable t) {
            Diorama.LOGGER.error("Couldn't set up GPU baking; tracing tiles instead", t);
         }
      }

      return this.usable;
   }

   private void create(GLCapabilities caps) throws IOException {
      boolean zeroToOne = (caps.OpenGL45 || caps.GL_ARB_clip_control) && GL33C.glGetInteger(37725) == 37727;
      String depth = zeroToOne ? "DIORAMA_DEPTH_ZERO_TO_ONE" : null;
      String vsh = shader("core/bake.vsh");
      String fsh = shader("core/bake.fsh");
      this.skyProgram = program(vsh, fsh, Map.of("Voxels", 0), "SKY_PASS", depth);
      this.shadowProgram = program(vsh, fsh, Map.of(), "SHADOW", depth);
      Map<String, Integer> faces = Map.of("ShadowMap", 0, "Sky", 1);
      this.opaqueProgram = program(vsh, fsh, Map.of("ShadowMap", 0, "Sky", 1, "Voxels", 2), null, depth);
      this.tintedProgram = program(vsh, fsh, faces, "TINTED", depth);
      this.waterProgram = program(vsh, fsh, Map.of("ShadowMap", 0, "Sky", 1, "Voxels", 2), "WATER", depth);
      Map<String, Integer> composite = new LinkedHashMap<>();
      String[] names = new String[]{"SceneColor", "SceneDepth", "SceneAux", "Tint", "WaterColor", "WaterDepth"};

      for (int i = 0; i < names.length; i++) {
         composite.put(names[i], i);
      }

      this.compositeProgram = program(shader("core/bake_composite.vsh"), shader("core/bake_composite.fsh"), composite, null, depth);
      int size = 512;
      this.shadowTexture = texture(36012, 2048, 2048, 6402, 5126);
      this.sceneColor = texture(34842, 1028, 1028, 6408, 5131);
      this.sceneAux = texture(34842, 1028, 1028, 6408, 5131);
      this.sceneDepth = texture(36012, 1028, 1028, 6402, 5126);
      this.tint = texture(34842, 1028, 1028, 6408, 5131);
      this.waterColor = texture(34842, 1028, 1028, 6408, 5131);
      this.waterDepth = texture(36012, 1028, 1028, 6402, 5126);
      this.sky = texture(33321, 2048, 1024, 6403, 5121);
      this.voxels = texture(33334, 4096, 512, 36244, 5125);
      this.output = texture(32856, size, size, 6408, 5121);
      this.outputInfo = texture(33323, size, size, 33319, 5121);
      this.skyFbo = framebuffer(0, this.sky);
      this.shadowFbo = framebuffer(this.shadowTexture);
      this.sceneFbo = framebuffer(this.sceneDepth, this.sceneColor, this.sceneAux);
      this.tintFbo = framebuffer(this.sceneDepth, this.tint);
      this.waterFbo = framebuffer(this.waterDepth, this.waterColor);
      this.outputFbo = framebuffer(0, this.output, this.outputInfo);
      this.ubo = GL33C.glGenBuffers();
      this.faceVbo = GL33C.glGenBuffers();
      this.casterVbo = GL33C.glGenBuffers();
      this.cornerVbo = GL33C.glGenBuffers();
      GL33C.glBindBuffer(34962, this.cornerVbo);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         GL33C.glBufferData(34962, stack.bytes(new byte[]{0, 0, 0, 0, -1, 0, 0, 0, -1, -1, 0, 0, 0, -1, 0, 0}), 35044);
         this.indexBuffer = GL33C.glGenBuffers();
         this.faceVao = GL33C.glGenVertexArrays();
         GlState.bindVertexArray(this.faceVao);
         GL33C.glBindBuffer(34963, this.indexBuffer);
         GL33C.glBufferData(34963, stack.shorts(new short[]{0, 1, 2, 2, 3, 0}), 35044);
      } catch (Throwable var14) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (stack != null) {
         stack.close();
      }

      this.corners();

      for (int a = 1; a < ATTRIBUTES.length; a++) {
         GL33C.glEnableVertexAttribArray(a);
         divisor(a, 1);
      }

      this.pointFaces(0);
      this.casterVao = GL33C.glGenVertexArrays();
      GlState.bindVertexArray(this.casterVao);
      GL33C.glBindBuffer(34963, this.indexBuffer);
      this.corners();
      GL33C.glBindBuffer(34962, this.casterVbo);
      GL33C.glEnableVertexAttribArray(1);
      divisor(1, 1);
      GL33C.glVertexAttribIPointer(1, 4, 5122, 16, 0L);
      GL33C.glEnableVertexAttribArray(6);
      divisor(6, 1);
      GL33C.glVertexAttribIPointer(6, 2, 5124, 16, 8L);
      this.emptyVao = GL33C.glGenVertexArrays();
      GlState.forgetBuffers();
      int error = GL33C.glGetError();
      if (error != 0) {
         throw new IllegalStateException("OpenGL error " + error + " setting up GPU baking");
      }
   }

   private static void divisor(int attribute, int divisor) {
      if (coreDivisor) {
         GL33C.glVertexAttribDivisor(attribute, divisor);
      } else {
         ARBInstancedArrays.glVertexAttribDivisorARB(attribute, divisor);
      }
   }

   private static void noSampler(int unit) {
      if (samplerObjects) {
         GL33C.glBindSampler(unit, 0);
      }
   }

   private void corners() {
      GL33C.glBindBuffer(34962, this.cornerVbo);
      GL33C.glEnableVertexAttribArray(0);
      GL33C.glVertexAttribPointer(0, 4, 5121, true, 4, 0L);
      divisor(0, 0);
   }

   private static int texture(int internalFormat, int width, int height, int format, int type) {
      int texture = GL33C.glGenTextures();
      GlState.activeTexture(0);
      GlState.bindTexture(texture);
      GL33C.glTexParameteri(3553, 10241, 9728);
      GL33C.glTexParameteri(3553, 10240, 9728);
      GL33C.glTexParameteri(3553, 10242, 33071);
      GL33C.glTexParameteri(3553, 10243, 33071);
      GL33C.glTexParameteri(3553, 33085, 0);
      GL33C.glTexParameteri(3553, 34892, 0);
      GL33C.glTexImage2D(3553, 0, internalFormat, width, height, 0, format, type, (ByteBuffer)null);
      return texture;
   }

   private static int framebuffer(int depth, int... colors) {
      int fbo = GL33C.glGenFramebuffers();
      GlState.bindFramebuffer(36160, fbo);
      if (depth != 0) {
         GL33C.glFramebufferTexture2D(36160, 36096, 3553, depth, 0);
      }

      int[] buffers = new int[colors.length];

      for (int i = 0; i < colors.length; i++) {
         GL33C.glFramebufferTexture2D(36160, 36064 + i, 3553, colors[i], 0);
         buffers[i] = 36064 + i;
      }

      if (colors.length == 0) {
         GL33C.glDrawBuffer(0);
         GL33C.glReadBuffer(0);
      } else {
         GL33C.glDrawBuffers(buffers);
      }

      int status = GL33C.glCheckFramebufferStatus(36160);
      if (status != 36053) {
         throw new IllegalStateException("GPU bake framebuffer incomplete: " + status);
      } else {
         return fbo;
      }
   }

   private static String shader(String name) throws IOException {
      String text = BLOCK_COMMENT.matcher(read("/assets/diorama/shaders/" + name)).replaceAll("");
      String include = Matcher.quoteReplacement(BLOCK_COMMENT.matcher(read("/assets/diorama/shaders/include/bake.glsl")).replaceAll(""));
      return INCLUDE.matcher(text).replaceAll(include);
   }

   private static String read(String path) throws IOException {
      try (InputStream in = GlBakeBackend.class.getResourceAsStream(path)) {
         if (in == null) {
            throw new IOException("missing " + path);
         } else {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
         }
      }
   }

   private static int program(String vsh, String fsh, Map<String, Integer> samplers, @Nullable String define, @Nullable String depth) {
      Map<String, Integer> outputs = new LinkedHashMap<>();
      Matcher m = OUTPUT.matcher(fsh);

      while (m.find()) {
         outputs.put(m.group(2), Integer.parseInt(m.group(1)));
      }

      String defines = (define == null ? "" : "#define " + define + "\n") + (depth == null ? "" : "#define " + depth + "\n");
      int vs = compile(35633, prepare(vsh, defines));
      int fs = compile(35632, prepare(fsh, defines));
      int program = GL33C.glCreateProgram();
      GL33C.glAttachShader(program, vs);
      GL33C.glAttachShader(program, fs);

      for (int a = 0; a < ATTRIBUTES.length; a++) {
         GL33C.glBindAttribLocation(program, a, ATTRIBUTES[a]);
      }

      for (Entry<String, Integer> output : outputs.entrySet()) {
         GL33C.glBindFragDataLocation(program, output.getValue(), output.getKey());
      }

      GL33C.glLinkProgram(program);
      GL33C.glDetachShader(program, vs);
      GL33C.glDetachShader(program, fs);
      GL33C.glDeleteShader(vs);
      GL33C.glDeleteShader(fs);
      if (GL33C.glGetProgrami(program, 35714) == 0) {
         String log = GL33C.glGetProgramInfoLog(program);
         GL33C.glDeleteProgram(program);
         throw new IllegalStateException("GPU bake shader didn't link (" + define + "): " + log);
      }

      int block = GL33C.glGetUniformBlockIndex(program, "DioramaBake");
      if (block != -1) {
         GL33C.glUniformBlockBinding(program, block, 30);
      }

      GlState.useProgram(program);

      for (Entry<String, Integer> sampler : samplers.entrySet()) {
         int location = GL33C.glGetUniformLocation(program, sampler.getKey());
         if (location >= 0) {
            GL33C.glUniform1i(location, sampler.getValue());
         }
      }

      return program;
   }

   private static String prepare(String source, String defines) {
      String text = LOCATION.matcher(source)
         .replaceAll("")
         .replace("#version 330", "#version 150")
         .replace("#extension GL_ARB_separate_shader_objects : require", "")
         .replace("gl_VertexIndex", "gl_VertexID");
      int line = text.indexOf(10);
      return text.substring(0, line + 1) + defines + text.substring(line + 1);
   }

   private static int compile(int type, String source) {
      int shader = GL33C.glCreateShader(type);
      GL33C.glShaderSource(shader, source);
      GL33C.glCompileShader(shader);
      if (GL33C.glGetShaderi(shader, 35713) == 0) {
         String log = GL33C.glGetShaderInfoLog(shader);
         GL33C.glDeleteShader(shader);
         throw new IllegalStateException("GPU bake shader didn't compile: " + log);
      } else {
         return shader;
      }
   }

   private record Readback(GpuTileBaker.Job job, int colors, int info, long fence, int startQuery, int endQuery, long recordNanos) {
   }

   private record Saved(
      int program,
      int vao,
      int arrayBuffer,
      int uniformBuffer,
      int uboBinding,
      int pack,
      int unpack,
      int drawFbo,
      int readFbo,
      int[] viewport,
      int activeTexture,
      int[] textures,
      int[] samplers,
      int[] pixelStore,
      boolean cull,
      boolean scissor,
      boolean depthTest,
      int depthFunc,
      boolean depthMask,
      boolean blend0,
      boolean blend1,
      int[] blendFunc,
      boolean[] colorMask
   ) {
      private static final int[] PIXEL_STORE = new int[]{3333, 3330, 3332, 3331, 3317, 3314, 3316, 3315};

      static GlBakeBackend.Saved take() {
         int[] viewport = new int[4];
         GL33C.glGetIntegerv(2978, viewport);
         int active = GL33C.glGetInteger(34016) - 33984;
         int[] textures = new int[6];
         int[] samplers = new int[6];

         for (int u = 0; u < 6; u++) {
            GlState.activeTexture(u);
            textures[u] = GL33C.glGetInteger(32873);
            samplers[u] = GlBakeBackend.samplerObjects ? GL33C.glGetInteger(35097) : 0;
         }

         GlState.activeTexture(active);
         int[] pixelStore = new int[PIXEL_STORE.length];

         for (int i = 0; i < PIXEL_STORE.length; i++) {
            pixelStore[i] = GL33C.glGetInteger(PIXEL_STORE[i]);
         }

         boolean[] colorMask = new boolean[4];
         MemoryStack stack = MemoryStack.stackPush();

         try {
            ByteBuffer mask = stack.malloc(4);
            GL33C.glGetBooleanv(3107, mask);

            for (int i = 0; i < 4; i++) {
               colorMask[i] = mask.get(i) != 0;
            }
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }

         int[] blendFunc = new int[]{GL33C.glGetInteger(32969), GL33C.glGetInteger(32968), GL33C.glGetInteger(32971), GL33C.glGetInteger(32970)};
         return new GlBakeBackend.Saved(
            GL33C.glGetInteger(35725),
            GL33C.glGetInteger(34229),
            GL33C.glGetInteger(34964),
            GL33C.glGetInteger(35368),
            GL33C.glGetIntegeri(35368, 30),
            GL33C.glGetInteger(35053),
            GL33C.glGetInteger(35055),
            GL33C.glGetInteger(36006),
            GL33C.glGetInteger(36010),
            viewport,
            active,
            textures,
            samplers,
            pixelStore,
            GL33C.glIsEnabled(2884),
            GL33C.glIsEnabled(3089),
            GL33C.glIsEnabled(2929),
            GL33C.glGetInteger(2932),
            GL33C.glGetBoolean(2930),
            GL33C.glIsEnabledi(3042, 0),
            GL33C.glIsEnabledi(3042, 1),
            blendFunc,
            colorMask
         );
      }

      static void plainPixelStore() {
         for (int i = 0; i < PIXEL_STORE.length; i++) {
            int name = PIXEL_STORE[i];
            GL33C.glPixelStorei(name, name != 3333 && name != 3317 ? 0 : 4);
         }
      }

      void restore() {
         for (int u = 0; u < 6; u++) {
            GlState.activeTexture(u);
            GlState.bindTexture(this.textures[u]);
            if (GlBakeBackend.samplerObjects) {
               GL33C.glBindSampler(u, this.samplers[u]);
            }
         }

         GlState.activeTexture(this.activeTexture);
         GlState.useProgram(this.program);
         GlState.bindVertexArray(this.vao);
         GL33C.glBindBuffer(34962, this.arrayBuffer);
         GL33C.glBindBufferBase(35345, 30, this.uboBinding);
         GL33C.glBindBuffer(35345, this.uniformBuffer);
         GL33C.glBindBuffer(35051, this.pack);
         GL33C.glBindBuffer(35052, this.unpack);
         GlState.bindFramebuffer(36009, this.drawFbo);
         GlState.bindFramebuffer(36008, this.readFbo);
         GlState.viewportAt(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);

         for (int i = 0; i < PIXEL_STORE.length; i++) {
            GL33C.glPixelStorei(PIXEL_STORE[i], this.pixelStore[i]);
         }

         GlState.cull(this.cull);
         GlState.scissor(this.scissor);
         GlState.depthTest(this.depthTest);
         GlState.depthFunc(this.depthFunc);
         GlState.depthMask(this.depthMask);
         GlState.blendFuncSeparate(this.blendFunc[0], this.blendFunc[1], this.blendFunc[2], this.blendFunc[3]);
         GlState.restoreBlend(this.blend0, this.blend1);
         GlState.colorMask(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);
         GlState.forgetBuffers();
      }
   }
}
