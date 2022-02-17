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

displayRoleElement(["GUESSADD"],"add")

/**
 * @method tableDataList 数据渲染表格
 * @param {Array} list 用于渲染表格的数据
 * */
function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '尚未开始',
            height:500,
            loading:false,
            cols: [[
                {field: 'id', title: '竞猜ID',align:"center"},
                {field: 'title', title: '竞猜标题',align:"center"},
                {field: 'guessType', title: '竞猜类型',align:"center"},
                {field: 'showStartTime', title: '展示开始时间',align:"center"},
                {field: 'showEndTime', title: '展示结束时间',align:"center"},
                {field: 'wagerStartTime', title: '下注开始时间',align:"center"},
                {field: 'wagerEndTime', title: '下注结束时间',align:"center"},
                {field: 'sort', title: '排序',align:"center"},
                {field:'state',title:"当前状态",align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center"}
            ]],
            id: 'testReload',
            page: false
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("guessDetails","id",data.id)
            }
        });
        roleTableBtn(["GUESSEDIT"],"detail")
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
                var dateStart = $("#test1").val();
                var dateEnd = $("#test2").val();
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
                if(!first){
                    $.get({
                        url: window.ioApi.guess.getNotStartGuess,
                        data: sendObj,
                        success: function (res) {
                            tableDataList(guessFilter(res.data))
                            count = res.count;
                            page = obj.curr;
                            getPage(res.count,obj.curr)
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

$("#searchBtn").on("click",function () {
    sendObj = {};
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
        url: window.ioApi.guess.getNotStartGuess,
        data: sendObj,
        success: function (res) {
            console.log(res);
            tableDataList(guessFilter(res.data));
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
})


$("#resetBtn").on("click",function () {
    location.reload();
})

$("#add").on("click",function () {
    jumpUrl("guessAdditions")
})

getData()
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
        url: window.ioApi.guess.getNotStartGuess,
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
                ,content: '获取竞猜列表失败，请联系管理员'
            });
        }
    })
}
