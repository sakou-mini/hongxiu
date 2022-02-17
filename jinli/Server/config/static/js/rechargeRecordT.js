var page = 1;
var size = 10;
var count = 0;
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1',
    });
    laydate.render({
        elem: '#test2',
    });
});

$.post({
    url:window.ioApi.platform.getRechargeRecord,
    data:getSearchData(),
    success:function (res) {
        console.log(res);
        count = res.total
        tableDataList(dataFilter(res.content))
        getPage()
    }
})

function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '竞猜列表',
            height:500,
            loading:false,
            cols: [[
                {field: 'displayName', title: '用户名称(用户ID)',align:"center"},
                {field: 'rechargeCoin', title: '充值金额',align:"center"},
                {field: 'rechargeTime', title: '充值时间',align:"center"},
                {field: 'leftCoin', title: '当前余额',align:"center"},
                {field: 'rechargeTotalAmount', title: '累计充值',align:"center"},
                {field: 'platformTag', title: '平台',align:"center"},
                // {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload',
            page: false
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
        });
    })
}

$("#searchBtn").on("click",function () {
    page =1
    $.post({
        url:window.ioApi.platform.getRechargeRecord,
        data:getSearchData(1),
        success:function (res) {
            console.log(res);
            count = res.total
            tableDataList(dataFilter(res.content))
        }
    })
})


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
                $.post({
                    url:window.ioApi.platform.getRechargeRecord,
                    data:getSearchData(),
                    success:function (res) {
                        console.log(res);
                        count = res.total
                        tableDataList(dataFilter(res.content))
                    }
                })
            }
        });
    })
}

function getSearchData(p) {
    var data = {
        page: p||page,
        size,
        userId:$("#userId").val(),
        displayName:$("#displayName").val(),
        platform:2
    };
    $("#test1").val() === "" ? data.startTime = "" : data.startTime = isIE()?
        new Date(( $("#test1").val()+" 00:00:00").replace(/-/g, '/')).getTime():new Date(($("#test1").val()+" 00:00:00")).getTime()
    $("#test2").val() === "" ? data.endTime = "" : data.endTime = isIE()?
        new Date(( $("#test2").val()+" 00:00:00").replace(/-/g, '/')).getTime():new Date(($("#test2").val()+" 00:00:00")).getTime()
    return data
}

function dataFilter(list) {
    var arr = [];
    for(var i=0;i<list.length;i++){
        var d = {}
        d.id = list[i].userSummary.userId
        d.displayName = list[i].userSummary.displayName + "("+list[i].userSummary.userId+")"
        d.leftCoin =  list[i].leftCoin
        d.rechargeCoin = list[i].rechargeCoin
        d.rechargeTime = dateFormat("YYYY-mm-dd HH:MM:SS",list[i].rechargeTime)
        d.rechargeTotalAmount =list[i].rechargeTotalAmount
        d.platformTag = getFlatformForNum(list[i].platformTag)
        arr.push(d)
    }
    return arr
}