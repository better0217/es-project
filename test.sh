#!/bin/bash

# --- 配置 ---
API_URL="http://localhost:8080/work-orders"
MINUTES=5
REQUESTS_PER_MINUTE=20

# --- 模拟数据池 ---
PERSONS=("张伟" "李娜" "王强" "刘敏" "陈浩" "周芳" "吴磊" "赵静" "钱进" "孙悦")
ADDRESSES=(
    "黄浦区南京东路233号" "徐汇区衡山路811号" "长宁区愚园路1398弄" "静安区威海路755号"
    "普陀区中山北路3663号" "虹口区四川北路1885号" "杨浦区五角场万达广场" "闵行区莘庄地铁站南广场"
    "宝山区牡丹江路1569号" "浦东新区世纪大道88号"
)
TITLES=(
    "共享单车乱停放问题" "人行道地砖破损" "小区垃圾分类不清" "行道树树枝过低"
    "路灯夜间不亮" "公交站台广告牌损坏" "广场噪音扰民" "下水道井盖松动"
    "沿街商铺占道经营" "建议增设人行横道"
)
CONTENTS=(
    "在地铁站出口，大量共享单车随意停放，严重影响了行人通行，希望有关部门能及时清理。"
    "靠近十字路口的人行道上有多块地砖已经碎裂，高低不平，晚上走路很容易绊倒，存在安全隐患。"
    "我们小区的垃圾回收点，干湿垃圾经常混在一起，希望物业能加强管理和引导。"
    "路边的梧桐树长得太茂盛了，有些树枝已经垂到快碰到行人头顶的高度，希望能修剪一下。"
    "这条路上的路灯已经连续三个晚上没有亮了，给夜间出行带来了很大的不便，请尽快维修。"
    "公交站的候车亭顶棚上，有一块广告牌的角已经翘起来了，刮大风的时候很危险。"
    "每天晚上都有人用高音喇叭在广场上唱歌，声音太大，影响了周边居民的正常休息。"
    "路中间的一个下水道井盖，每次有车开过去都会发出‘哐当’的巨响，听起来像是松了。"
    "很多水果店和早餐店把摊位摆到了人行道上，导致本来就不宽的路更难走了。"
    "这个路口车速很快，但没有斑马线，行人过马路非常危险，强烈建议增设一条人行横道。"
)
STATUSES=("新建" "待处理")

# --- 测试脚本主逻辑 ---

echo "🚀 开始5分钟压力测试，总计 $(($MINUTES * $REQUESTS_PER_MINUTE)) 个工单..."

for (( m=1; m<=$MINUTES; m++ ))
do
    echo "--- ⏱️  第 $m 分钟 ---"
    for (( i=1; i<=$REQUESTS_PER_MINUTE; i++ ))
    do
        # 随机组合数据
        PERSON=${PERSONS[$RANDOM % ${#PERSONS[@]}]}
        ADDRESS=${ADDRESSES[$RANDOM % ${#ADDRESSES[@]}]}
        TITLE=${TITLES[$RANDOM % ${#TITLES[@]}]}
        CONTENT=${CONTENTS[$RANDOM % ${#CONTENTS[@]}]}
        STATUS=${STATUSES[$RANDOM % ${#STATUSES[@]}]}
        SERIAL_NUM="SN-LOADTEST-M$m-I$i"

        # 使用jq工具动态构建JSON，更健壮
        JSON_PAYLOAD=$(jq -n \
            --arg serialNum "$SERIAL_NUM" \
            --arg rqstPerson "$PERSON" \
            --arg rqstAddress "$ADDRESS" \
            --arg rqstTitle "$TITLE" \
            --arg rqstContent "$CONTENT" \
            --arg cstatus "$STATUS" \
            '{serialNum: $serialNum, rqstPerson: $rqstPerson, rqstAddress: $rqstAddress, rqstTitle: $rqstTitle, rqstContent: $rqstContent, cstatus: $cstatus}')

        echo "发送第 $i 个工单: $TITLE"

        # 【已修正】发送curl请求，移除了错误的 "D"
        curl -s -o /dev/null -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -d "$JSON_PAYLOAD" &

        # 每分钟20个请求，大约每3秒一个
        sleep 3
    done
    echo "--- 第 $m 分钟的20个请求已全部发送 ---"
done

echo "✅ 测试完成！"


