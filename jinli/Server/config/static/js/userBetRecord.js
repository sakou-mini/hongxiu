layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});

layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1'
    });
    laydate.render({
        elem: '#test2'
    });
});
var sendObj = {};
var page = 1;
var size = 10;
var count = 0;

function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            jump: function(obj, first){
                page = obj.curr;
                if(!first){
                    console.log(obj.curr);
                    analogData();
                }
            }
        });
    });
}

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
            ,height: 500
            ,data: list
            ,cellMinWidth: 180
            ,cols: [[ //表头
                {field: 'time', title: '时间'}
                ,{field: 'roomId', title: '直播间',}
                ,{field: 'play', title: '玩法'}
                ,{field: 'region', title: '投注区域', width:"35%"}
                ,{field: 'option', title: '开奖结果', width:"20%"}
                ,{field: 'win', title: '胜负情况'}
                ,{field: 'coin', title: '当前金额'}
            ]]
        });
    });
}

function analogData() {
    $.get({
        url:window.ioApi.user.queryUserBetDetail,
        data:{
            userId:GetQueryString("userId"),
            page:page,
            size:size
        },
        success:function (res) {
            $("#displayName").text(window.localStorage.getItem('displayName'))
            console.log(res);
            var data = [];
            var b = res.data;
            for(var i =0;i<b.length;i++){
                var d = {};
                d.time = dateFormat("YYYY-mm-dd HH:MM:SS",b[i].time);
                d.roomId=b[i].roomDisplayId;
                var bk
                if(b[i].bankerGame){
                    bk="(上庄)"
                }else {
                    bk="(官方庄)"
                }
                d.play=gameTypeFormat(b[i].gameType)+bk;
                var betArr = [];
                for(var key  in b[i].betRecord){
                    var betItem  =betTypeFormat(key)+"("+b[i].betRecord[key]+")";
                    betArr.push(betItem)
                }
                d.region=betArr.join("、");
                var gameResult = [];
                for(var x=0;x<b[i].gameResult.length;x++){
                    gameResult.push(betTypeFormat(b[i].gameResult[x]))
                }
                d.option=gameResult.join("、");
                d.win=b[i].win;
                d.coin=b[i].userCoin;
                data.push(d)
            }
            tableData(data);
            count = res.count;
            getPage()
        }
    })
}