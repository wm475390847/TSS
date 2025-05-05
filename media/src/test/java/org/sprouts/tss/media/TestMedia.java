package org.sprouts.tss.media;

/**
 * @author wangmin
 * @date 2023/7/18 15:46
 */
public class TestMedia {

    public static void main(String[] args) {
        double result = new ImageComparator.Builder()
                .targetImagePath("chaohu-media/src/main/resources/pic/1.jpeg")
                .baseImagePath("chaohu-media/src/main/resources/pic/2.jpeg")
                .outputPath("chaohu-media/src/main/resources/pic/result.jpeg")
                .build()
                .compare();
        System.err.println(result);
    }
}
