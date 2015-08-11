#!/bin/bash

# TS视频提取关键帧并存储为MP4格式文件

for i in `ls $1`;
do
    echo $i;
    ffmpeg -i $1/$i -vcodec libx264 -vf select="eq(pict_type\,PICT_TYPE_I)" -crf 30 $2/${i:0:0-3}.mp4
done
