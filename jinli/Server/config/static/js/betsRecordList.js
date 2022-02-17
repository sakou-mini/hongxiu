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
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                size =obj.limit;
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

$("#resetBtn").on("click",function (){
    location.reload();
});

analogData();

function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            // ,height: 500
            ,data: list
            ,limit:size
            ,cellMinWidth: 180
            ,cols: [[ //表头
                {field: 'avatarUrl', title: '主播昵称',templet:'#imgTemplet',width:250},
                {field: 'time', title: '时间',align:"center"}
                ,{field: 'roomId', title: '直播间',align:"center"}
                ,{field: 'play', title: '玩法',align:"center"}
                ,{field: 'region', title: '投注区域', width:"35%",align:"center"}
                ,{field: 'option', title: '开奖结果', width:"35%",align:"center"}
                ,{field: 'win', title: '胜负情况',align:"center"}
                ,{field: 'coin', title: '当前金额',align:"center"}
            ]]
        });
    });
}

function analogData() {
    $.get({
        url:window.ioApi.revenueAnalysis.getPageRecordList,
        data:{
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
                var bk;
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
                d.head=window.config.imgUrl+b[i].avatarUrl;
                d.id=b[i].betUserId;
                d.displayName=b[i].displayName;
                data.push(d)
            }
            tableData(data);
            count = res.count;
            getPage()
        }
    })
}

$.get({
    url:window.ioApi.revenueAnalysis.getBetRecordSummary,
    success:function (res) {
        console.log(res);
        $("#allBetAmount").text(res.allBetAmount);
        $("#allBetCount").text(res.allBetCount);
        $("#todayBetAmount").text(res.todayBetAmount);
        $("#todayBetCount").text(res.todayBetCount);
        $("#todayBetProfit").text(res.todayBetAmount/100);
        $("#allBetProfit").text(res.allBetAmount/100);
        $("#aBACCARAT").text(res.allGameIncome.BACCARAT);
        $("#aGOLDENFLOWER").text(res.allGameIncome.GOLDENFLOWER);
        $("#aLONGHU").text(res.allGameIncome.LONGHU);
        $("#aNIUNIU").text(res.allGameIncome.NIUNIU);
        $("#aREDBLACK").text(res.allGameIncome.REDBLACK);
        $("#tBACCARAT").text(res.todayGameIncome.BACCARAT);
        $("#tGOLDENFLOWER").text(res.todayGameIncome.GOLDENFLOWER);
        $("#tLONGHU").text(res.todayGameIncome.LONGHU);
        $("#tNIUNIU").text(res.todayGameIncome.NIUNIU);
        $("#tREDBLACK").text(res.todayGameIncome.REDBLACK)
    }
});
