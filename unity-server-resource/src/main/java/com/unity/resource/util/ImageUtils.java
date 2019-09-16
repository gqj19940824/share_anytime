package com.unity.resource.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 压缩图片工具类
 * <p>
 * create by zhangxiaogang at 2019/2/27 17:06
 */
@Slf4j
public class ImageUtils {


    public static String DEFAULT_PREVFIX = "thumb_";
    public static Boolean DEFAULT_FORCE = false;//建议该值为false

    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     *
     * @param imagePath 原图片路径
     * @param newPath   新图片路径
     * @param w         缩略图宽
     * @param h         缩略图高
     * @param force     是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public static void thumbnailImage(File imgFile, String newPath, int w, int h, boolean force) {
        //File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if (imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0) {
                    log.info("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }
                Image img = ImageIO.read(imgFile);
                if (!force) {
                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if ((width * 1.0) / w < (height * 1.0) / h) {
                        if (width > w) {
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
                        }
                    } else {
                        if (height > h) {
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
                            log.info("change image's width, width:{}, height:{}." + w + h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                //String p = imgFile.getPath();
                // 将图片保存在原目录并加上前缀
                //ImageIO.write(bi, suffix, new File(newPath + File.separator + prevfix + imgFile.getName()));
                ImageIO.write(bi, suffix, new File(newPath));
                log.info("缩略图在原路径下生成成功");
            } catch (IOException e) {
                log.info("generate thumbnail image failed.");
            }
        } else {
            log.info("the image is not exist.");
        }
    }

    /*public static void main(String[] args) {
        new ImageUtils().thumbnailImage("C:\\Users\\HP-PC\\Desktop\\Images\\Images\\20190212174453.jpg", 113, 100,DEFAULT_PREVFIX,DEFAULT_FORCE);
    }*/
}