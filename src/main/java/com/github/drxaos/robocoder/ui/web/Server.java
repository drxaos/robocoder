package com.github.drxaos.robocoder.ui.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import org.apache.commons.io.IOUtils;
import sun.awt.image.IntegerInterleavedRaster;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;

public class Server extends AbstractVerticle {
    ImageSource source;

    public Server(ImageSource source) {
        this.source = source;
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.routeWithRegex("/(.+\\.(html|js|css))?").handler(ctx -> {
            try {
                String path = ctx.request().path().replace("/", "");
                if (path.isEmpty()) {
                    path = "index.html";
                }
                ctx.response().putHeader("Content-Type", "text/html; charset=utf-8").end(
                        IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(path))
                );
            } catch (Exception e) {
                e.printStackTrace();
                ctx.response().end("Error!");
            }
        });

        vertx.createHttpServer().requestHandler(router::accept).websocketHandler(ws -> {
            if (!ws.path().equals("/stream")) {
                ws.reject();
                return;
            }
            final String id = ws.textHandlerID();
            //System.out.println("registering new connection with id: " + id + "");

            //System.out.println("Starting streaming...");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            final boolean[] ready = new boolean[]{true};
            final int[] count = new int[]{0};

//            ws.closeHandler(event ->
//                    System.out.println("Done" + count[0])
//            );

            final BufferedImage[] prev = new BufferedImage[]{null};

            ws.handler(data -> {
                try {
                    if (!ready[0]) {
                        ws.close();
                        return;
                    }
                    ready[0] = false;

                    String s = new String(data.getBytes());
                    if (s.equals("i")) {
                        // new image request
                        final long start = System.currentTimeMillis();

                        BufferedImage image = source.getImage();
                        BufferedImage diff = prev[0] == null ? image : diff(prev[0], image);
//                        BufferedImage diff = image;
                        if (diff != null) {
                            ImageIO.write(diff, "png", stream);
                            prev[0] = image;
                        }

                        byte[] ar = stream.toByteArray();
                        if (ar.length > 0) {
                            // send diff to client
                            //System.out.println("Frame " + count[0]++ + " / " + ar.length + " bytes / " + (System.currentTimeMillis() - start) + " ms");
                            ws.writeBinaryMessage(Buffer.buffer(ar));
                            ready[0] = true;
                            stream.reset();
                        } else {
                            // wait changes
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < 20; i++) {
                                            Thread.sleep(100);

                                            BufferedImage image = source.getImage();
                                            BufferedImage diff = prev[0] == null ? image : diff(prev[0], image);
                                            if (diff != null) {
                                                ImageIO.write(diff, "png", stream);
                                                prev[0] = image;
                                            }
                                            byte[] ar = stream.toByteArray();
                                            if (ar.length > 0) {
                                                //System.out.println("Frame " + count[0]++ + " / " + ar.length + " bytes / " + (System.currentTimeMillis() - start) + " ms");
                                                ws.writeBinaryMessage(Buffer.buffer(ar));
                                                ready[0] = true;
                                                stream.reset();
                                                return;
                                            }
                                        }
                                        ws.writeFinalTextFrame("e");
                                        ready[0] = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ws.close();
                                    }
                                }
                            }.start();
                        }
                    } else if (s.equals("s")) {
                        // image size request
                        BufferedImage image = source.getImage();
                        ws.writeFinalTextFrame("d" + image.getWidth() + "x" + image.getHeight());
                        ready[0] = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ws.close();
                }
            });
        }).listen(8888);
    }

    private BufferedImage diff(BufferedImage img1, BufferedImage img2) {
        int width1 = img1.getWidth(null);
        int width2 = img2.getWidth(null);
        int height1 = img1.getHeight(null);
        int height2 = img2.getHeight(null);

        if ((width1 != width2) || (height1 != height2)) {
            System.err.println("different image sizes");
        }

        BufferedImage res = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_ARGB);
        int[] data1 = ((IntegerInterleavedRaster) img1.getData()).getDataStorage();
        int[] data2 = ((IntegerInterleavedRaster) img2.getData()).getDataStorage();
        Raster rData = res.getData();
        int[] data3 = ((IntegerInterleavedRaster) rData).getDataStorage();

        long count = 0;
        for (int i = 0; i < data1.length; i++) {
            if (data1[i] == data2[i]) {
                data3[i] = 0;
            } else {
                data3[i] = data2[i];
                count++;
            }
        }
        res.setData(rData);
        return count > 0 ? res : null;
    }

}