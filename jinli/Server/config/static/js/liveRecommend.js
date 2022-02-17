var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var slotNum = 1;
var type = "0";
layui.use('form', function(){
    var form = layui.form;
    form.on('select(type)', function(data){
        type = data.value;
        page = 1;
        analogData()
    });
});

layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});

$("#resetBtn").on("click",function () {
    location.reload();
});

analogData();
function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            ,limit:count
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'slot', title: '推荐位权重（排序）'},
                {field: 'id', title: '主播名（主播ID）'},
                {field: 'roomId', title: '直播间ID'},
                {field: 'roomNum', title: '当前直播间人数'},
                {field: 'player', title: '当前直播间玩法'},
                {field: 'gameFlowing', title: '直播间游戏流水'},
                {field: 'giftFlowing', title: '直播间礼物流水'},
                {field: 'time', title: '到期时间'},
                {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === "c"){
                $("#name").val(data.slot);
                $("#coin").val(data.time)
                layer.open({
                    type:1,
                    area:['500px'],
                    title: '修改礼物信息'
                    ,content: $("#test"),
                    shade: 0,
                    btn: ['提交']
                    ,btn1: function(index, layero){
                        layer.closeAll();
                    },
                    cancel: function(layero,index){
                        layer.closeAll();
                    }
                });
            }
        });
    });
}

function analogData() {
    slotNum = (page-1)*10
    count = 30;
    var data = [];
    var play = ["百家乐","百人牛牛","炸金花","红黑大战","龙虎斗","交友","萌宠","颜值","跳舞","唱歌"]
    var userName = ["德贵","阿晴","阿巴阿巴","罗德岛的博士","提瓦特的旅行者","格里芬的指挥官","休伯利安的舰长","镇守府的提督","兰德索尔的骑士君","迦勒底的御主"]
    for(var i = 0;i<30;i++){
        var d = {}
        d.id =  userName[Math.floor(Math.random()*userName.length)]+"("+randomNum(100000,999999)+")";
        d.roomId = randomNum(10000,99999)
        slotNum++
        d.slot = slotNum;
        d.player = play[Math.floor(Math.random()*play.length)];
        d.gameFlowing = randomNum(10000,9999999);
        d.giftFlowing = randomNum(10000,9999999);
        d.roomNum =  randomNum(100,100000);
        d.time = dateFormat("YYYY-mm-dd HH:MM:SS",new Date().getTime() + randomNum(10000,1000000))
        data.push(d)
    }
    tableData(data);

}