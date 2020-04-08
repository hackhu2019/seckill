package com.hackhu.seckill.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author hackhu
 * @date 2020/3/27
 */
public class CodeUtils {
    // 定义图片宽度
    private static int width = 90;
    // 定义图片高度
    private static int height = 20;
    // 定义图片验证码个数
    private static int codeCount = 4;
    // 验证码坐标
    private static int codeX = 15;
    private static int codeY = 16;
    // 验证码字体大小
    private static int fontWeight = 18;
    private static char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 生成验证码
     */
    public static Map<String, Object> generateCodeAndPic() {
        // 定义图像
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics gd = bufferedImage.getGraphics();
        // 创建随机数生成器
        Random rd = new Random();
        // 设置图像背景
        gd.setColor(Color.white);
        gd.fillRect(0, 0, width, height);
        // 创建字体
        Font font = new Font("Fixedsys", Font.BOLD, fontWeight);
        gd.setFont(font);
        // 描绘边框
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);
        // 生成干扰线
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 30; i++) {
            int x = rd.nextInt(width);
            int x1 = rd.nextInt(12);
            int y = rd.nextInt(height);
            int y1 = rd.nextInt(12);
            gd.drawLine(x, y, x1, y1);
        }
        // randomCode 记录验证码
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        // 产生验证码
        for (int i = 0; i < codeCount; i++) {
            String code = String.valueOf(codeSequence[rd.nextInt(codeSequence.length)]);
            red = rd.nextInt(255);
            green = rd.nextInt(255);
            blue = rd.nextInt(255);
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * codeX, codeY);
            randomCode.append(code);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("codePic", randomCode);
        return map;
    }
}
