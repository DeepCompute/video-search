
## 提取视频关键帧

$ ffmpeg -i test-videos/test1.ts -vf select="eq(pict_type\\,PICT_TYPE_I)" -vsync 2 -s 640x480 -f image2 output/thumbnails-%02d.jpeg

## 将关键帧转换成MP4视频文件

$ ffmpeg -r 2 -f image2 -s 640x480 -i output/thumbnails-%02d.jpeg -vcodec libx264 -crf 25  test.mp4

## 将其他视频转换成MP4

$ ffmpeg -i test1.ts -vcodec libx264 -crf 25 test.mp4

注意：将ts转换成mp4后，存储会减少很多，如：145M到28M，缩小到1/5，视频质量没有影响。

## 将TS视频提取关键帧，并转换成MP4

$ ffmpeg -i test1.ts -vcodec libx264 -vf select="eq(pict_type\,PICT_TYPE_I)" -crf 30 test.mp4




