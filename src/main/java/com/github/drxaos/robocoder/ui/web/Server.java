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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;

public class Server extends AbstractVerticle {
    ImageSource source;

    public Server(ImageSource source) {
        this.source = source;
    }

    HashSet<String> viewers = new HashSet<>();

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
            viewers.add(id);

            //System.out.println("Starting streaming...");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            final boolean[] ready = new boolean[]{true};
            //final int[] count = new int[]{0};

            ws.closeHandler(event -> {
                //System.out.println("Done" + count[0]);
                viewers.remove(id);
            });

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
                        //final long start = System.currentTimeMillis();

                        BufferedImage image = source.getImage();
                        sendDiffs(prev[0], image, stream);
                        prev[0] = image;

                        byte[] ar = stream.toByteArray();
                        if (ar.length > 0) {
                            // send diff to client
                            //System.out.println("Frame " + count[0]++ + " / " + ar.length + " bytes / " + (System.currentTimeMillis() - start) + " ms");
                            ws.writeBinaryMessage(Buffer.buffer(ar));
                            stream.reset();
                        }
                        ws.writeFinalTextFrame("e");
                        ready[0] = true;
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

    public int countViewers() {
        return viewers.size();
    }

    private void sendDiffs(BufferedImage img1, BufferedImage img2, OutputStream out) throws IOException {
        int width2 = img2.getWidth(null);
        int height2 = img2.getHeight(null);
        int height1 = img1 == null ? height2 : img1.getHeight(null);
        int width1 = img1 == null ? width2 : img1.getWidth(null);

        if ((width1 != width2) || (height1 != height2)) {
            System.err.println("different image sizes");
        }

        int xParts = 10;
        int yParts = 10;
        Integer[][] lefts = new Integer[xParts][yParts];
        Integer[][] rights = new Integer[xParts][yParts];
        Integer[][] tops = new Integer[xParts][yParts];
        Integer[][] bottoms = new Integer[xParts][yParts];

        BufferedImage res = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_ARGB);
        int[] data1 = img1 == null ? null : ((IntegerInterleavedRaster) img1.getData()).getDataStorage();
        int[] data2 = ((IntegerInterleavedRaster) img2.getData()).getDataStorage();
        Raster rData = res.getData();
        int[] data3 = ((IntegerInterleavedRaster) rData).getDataStorage();

        int count = 0;
        for (int i = 0; i < data2.length; i++) {
            if (data1 == null || data1[i] != data2[i]) {
                int x = i % width1;
                int y = i / width1;
                int xPart = x / (width1 / xParts);
                int yPart = y / (height1 / yParts);
                if (lefts[xPart][yPart] == null || lefts[xPart][yPart] > x) {
                    lefts[xPart][yPart] = x;
                }
                if (tops[xPart][yPart] == null || tops[xPart][yPart] > y) {
                    tops[xPart][yPart] = y;
                }
                if (rights[xPart][yPart] == null || rights[xPart][yPart] < x) {
                    rights[xPart][yPart] = x;
                }
                if (bottoms[xPart][yPart] == null || bottoms[xPart][yPart] < y) {
                    bottoms[xPart][yPart] = y;
                }
                data3[i] = data2[i];
                count++;
            } else {
                data3[i] = 0;
            }
        }
        res.setData(rData);
        ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
        if (count > 0) {
            for (int x = 0; x < xParts; x++) {
                for (int y = 0; y < yParts; y++) {
                    if (lefts[x][y] != null) {
                        Integer left = lefts[x][y];
                        Integer top = tops[x][y];
                        Integer right = rights[x][y] + 1;
                        Integer bottom = bottoms[x][y] + 1;
                        BufferedImage subimage = res.getSubimage(left, top, right - left, bottom - top);
                        imgStream.reset();
                        ImageIO.write(subimage, "png", imgStream);
                        byte[] imgBytes = imgStream.toByteArray();
                        byte[] infoBytes = ByteBuffer.allocate(12).putInt(left).putInt(top).putInt(imgBytes.length).array();
                        out.write(infoBytes);
                        out.write(imgBytes);
                    }
                }
            }
        }
    }
}