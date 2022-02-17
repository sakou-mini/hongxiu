layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});

var sendObj = {};
var page = 1;
var size = 10;
var count = 0;


layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});


function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '每月数据详情'
            ,limit:12
            ,loading:false
            , cols: [[
                {field: 's', title: '排序',align:"center"},
                {field: 'name', title: '用户昵称',align:"center"},
                {field: 'id', title: '用户ID',align:"center"},
                {field: 'vip', title: 'VIP等级',align:"center"},
                {field: 'roomId', title: '赠与直播间ID',align:"center"},
                {field: 'liveUserId', title: '赠与主播ID',align:"center"},
                {field: 'liwu', title: '赠与礼物ID',align:"center"},
                {field: 'num', title: '赠与数量',align:"center"},
                {field: 'j', title: '金额',align:"center"}
            ]],
            id: 'testReload'
        });
    });
}


$("#back").on("click",function () {
    window.history.go(-1)
})



function analogData() {
    $.get({
        url:window.ioApi.user.todayUserDataDetail,
        data:{
            page:page,
            size:size
        },
        success:function(res){
            count = res.total
            var list = []
            for(var i=0;i<res.content.length;i++){
                var d = {}
                d.s = i+1+((page-1)*10);
                d.name = res.content[i].userName;
                d.id= res.content[i].userId;
                d.vip = res.content[i].vipLevel;
                d.roomId = res.content[i].giftOfRoomId;
                d.liveUserId = res.content[i].liveUserId;
                d.liwu = res.content[i].giftId;
                d.num = res.content[i].sendNum;
                d.j=res.content[i].totalPrice;
                list.push(d)
            }
            tableData(list);
            getPage()
        }
    })
}


getPage();
analogData();
function getPage() {
    layui.use('laypage', function() {
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count, //数据总数，从服务端得到
            limit: size,
            curr: page,
            jump: function (obj, first) {
                page = obj.curr;
                if(!first){
                    analogData();
                    getPage();
                }
            }
        });
    })
}

$.get({
    url:window.ioApi.user.todayUserDataSummary,
    success:function(res){
        $("#d-IP_NUM").text(res.statisticItems.IP_NUM);
        $("#d-REGISTER_NUM").text(res.statisticItems.REGISTER_NUM);
    }
})