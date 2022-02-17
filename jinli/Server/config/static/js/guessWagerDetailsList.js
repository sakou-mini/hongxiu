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

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null && r[2] != "false")
        return unescape(r[2]);
    return false;
}

layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});
var page = 1;
var size = 10;
var count = 0;

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
                        url:window.ioApi.guess.getGuessWagerListByGuessId,
                        data:{
                            id:getQueryString("id"),
                            page:page,
                            size:size
                        },
                        success:function (res) {
                            tableDataList(wagerListFilter(res.data));
                            count = res.count;
                            page = obj.curr;
                            getPage();
                        },
                        error:function () {
                            layer.open({
                                title: '错误信息'
                                ,content: '服务器错误，请联系管理员'
                            });
                        }
                    })
                }
            }
        });
    })
}


function tableDataList(list) {
    console.log(list);
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '下注详情',
            height:500,
            loading:false,
            cols: [[
                {field: 'userId', title: '玩家id'},
                {field: 'orderNum', title: '订单号'},
                {field: 'displayName', title: '玩家昵称'},
                {field: 'wagerList', title: '下注选项'},
                {field: 'totalCoin', title: '下注总金额'}
            ]],
            id: 'testReload',
            page: false
        });
    })
}

function wagerListFilter(data) {
    for(var i=0;i<data.length;i++){
        var wagerMsg = [];
        for(var key in data[i].wagerList){
            var wagerMsgItem = data[i].wagerList[key].optionContent+"："+ data[i].wagerList[key].betNum+"";
            wagerMsg.push(wagerMsgItem);
        }
        data[i].wagerList = wagerMsg.join("、");
        data[i].wagerTime = formatDate(data[i].wagerTime);
    }
    return data;
}

getData()
function getData() {
    $.get({
        url: window.ioApi.guess.getGuessWagerListByGuessId,
        data: {
            id:getQueryString("id"),
            page:page,
            size:size
        },
        success: function (res) {
            console.log(res);
            tableDataList(wagerListFilter(res.data));
            count = res.count;
            getPage();
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '获取下注详情错误，请联系管理员'
            });
        }
    })
}