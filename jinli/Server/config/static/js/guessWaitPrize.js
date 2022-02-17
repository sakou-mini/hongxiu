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

function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '待开奖',
            height:500,
            loading:false,
            cols: [[
                {field: 'id', title: '竞猜ID',align:"center"},
                {field: 'title', title: '竞猜标题',align:"center"},
                {field: 'total', title: '下单数量',align:"center"},
                {field: 'totalCoin', title: '竞猜总金额',align:"center"},
                {field: 'showEndTime', title: '展示结束时间',align:"center"},
                {field: 'profit', title: '收益',align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center"}
            ]],
            id: 'testReload',
            page: false
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("guessDrawPrize","id",data.id)
            }
        });
    })
}

getPage();

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
                    $.get({
                        url: window.ioApi.guess.getWaitPrizeList,
                        data: {
                            page:page,
                            size:size
                        },
                        success: function (res) {
                            console.log(res);
                            tableDataList(guessFilter(res.data,true));
                            count = res.count;
                            page = obj.curr;
                            getPage();
                        },
                        error:function () {
                            layer.open({
                                title: '错误信息'
                                ,content: '获取竞猜错误，请联系管理员'
                            });
                        }
                    })
                }
            }
        });
    })
}


getData();
function getData() {
    var dateStart = new Date($("#test1").val()).getTime();
    var dateEnd = new Date($("#test2").val()).getTime();
    var guessType = $("#violationType").val();
    var sortItem = $("#handleSel").val();
    if (dateStart && dateEnd) {
        sendObj.startTime = dateStart;
        sendObj.endTime = dateEnd
    }
    sendObj.guessType = guessType;
    sendObj.sortItem = sortItem;
    sendObj.page = page;
    sendObj.size = size;
    $.get({
        url: window.ioApi.guess.getWaitPrizeList,
        data: sendObj,
        success: function (res) {
            console.log(res);
            tableDataList(guessFilter(res.data));
            count = res.count;
            getPage()
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '获取竞猜错误，请联系管理员'
            });
        }
    })
}