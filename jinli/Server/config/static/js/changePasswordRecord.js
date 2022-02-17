var page = 1;
var size = 10;
var count = 0;
var accountName=null;
var displayName = null;
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
/**
 * @method tableDataList 渲染表格
 * @param {Array} list 渲染表格需要的数据
 * */
function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '动态审核列表'
            ,height:500
            ,loading:false
            , cols: [[
                {field: 'accountName', title: '玩家信息',templet:'#imgTemplet'},
                {field: 'gameCoin', title: '玩家所持有金币'},
                {field: 'createTime', title: '玩家注册时间'},
                {field: 'backOfficeName', title: '操作人'},
                {field: 'time', title: '操作时间'},
            ]],
            id: 'testReload',
            page: false
        });

    })
}
$("#resetBtn").on("click",function () {
// 重置
    location.reload()
});

getRecordList();
function getRecordList() {
    $.get({
        url:window.ioApi.user.passwordRecord,
        data:{
            page:page,
            size:size
        },
        success:function (res) {
            console.log(res);
            tableDataList(filter(res.content));
            count = res.total;
            pageGat()
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '服务器错误，请联系管理员'
            });
        }
    })
}


//search
$("#searchBtn").on("click",function () {
    page=1;
    userId = $("#id").val().trim();
    displayName = $("#displayName").val().trim();
    $.get({
        url:window.ioApi.user.passwordRecord,
        data:{
            userId:userId,
            displayName:displayName,
            page:page,
            size:size
        },
        success:function (res) {
            tableDataList(filter(res.content));
            count = res.total;
            page = 1;
            pageGat()
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '服务器错误，请联系管理员'
            });
        }
    })
});


//page
function pageGat(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            jump: function(obj, first){
                if(!first){
                    page = obj.curr;
                    userId = $("#id").val().trim();
                    displayName = $("#displayName").val().trim();
                    $.get({
                        url:window.ioApi.user.passwordRecord,
                        data:{
                            userId:userId,
                            displayName:displayName,
                            page:page,
                            size:size
                        },
                        success:function (res) {
                            tableDataList(filter(res.content));
                            count = res.total;
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
    });
}

function filter(data) {
    for(var i =0 ;i<data.length;i++){
        data[i].time =   dateFormat("YYYY-mm-dd HH:MM-SS",data[i].time)
        data[i].createTime=dateFormat("YYYY-mm-dd HH:MM-SS",data[i].createTime)
    }
    console.log(data);
    return data;
}