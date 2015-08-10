
## 提取视频关键帧

$ ffmpeg -i test-videos/test1.ts -vf select="eq(pict_type\\,PICT_TYPE_I)" -vsync 2 -s 640x480 -f image2 output/thumbnails-%02d.jpeg

## 将关键帧转换成MP4视频文件

$ 