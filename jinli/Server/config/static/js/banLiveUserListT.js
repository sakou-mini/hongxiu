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

displayRoleElement(["ALLBANLIVEUSERLISTTUNLOCKLIVELIST"],"banUser")

var searchData = getFormData();
var nowPage = 1 ;
var size = 10
function pageGat(count,nowpage){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:nowpage,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                size =obj.limit;
                if(!first){
                    searchData = getFormData();
                    searchData.page=obj.curr;
                    searchData.size=obj.limit;

                    $.get({
                        url:window.ioApi.liveUser.queryBanLiveUser,
                        data:searchData,
                        success:function (res) {
                            dataCount = res.total;
                            tableData(userFilter(res.content));
                        },
                        error:function () {
                            layer.open({
                                title: '错误信息'
                                ,content: '获取封禁用户列表错误，请联系管理员'
                            });
                        }
                    });
                }
            }
        });
    });
}
firstInitTable();
function firstInitTable(){
    console.log("开始");
    searchData.page = 1
    searchData.size = size
    $.get({
        url:window.ioApi.liveUser.queryBanLiveUser,
        data:searchData,
        success:function (res) {
            dataCount = res.total;
            tableData(userFilter(res.content));
            pageGat(res.total,nowPage);
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '获取用户列表错误，请联系管理员'
            });
        }
    })
}

layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});

$("#searchBtn").on("click",function () {
    searchData = getFormData();
    searchData.page = 1
    searchData.size = size
    $.get({
        url:window.ioApi.liveUser.queryBanLiveUser,
        data:searchData,
        success:function (res) {
            dataCount = res.total;
            tableData(userFilter(res.content));
            pageGat(res.total,nowPage)


        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '获取主播列表错误，请联系管理员'
            });
        }
    })
});
$("#resetBtn").on("click",function (){
    location.reload();
});

var tData;
function tableData(list) {
    for(var i=0;i<list.length;i++){
        if(list[i].avatarUrl === null){
            list[i].head =window.config.imgUrl+"/images/avatar/default/img_avatar.png"
        }else {
            list[i].head = window.config.imgUrl+list[i].avatarUrl
        }
        list[i].auditTime = dateFormat("YYYY-mm-dd HH:MM:SS",list[i].auditTime)
    }
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            ,limit:size
            , title: '用户列表'
            ,loading:false
            ,done: function(res, curr, count){
                tData = [...res.data];
            }
            , cols: [[
                {type: 'checkbox'},
                {field: 'avatarUrl', title: '用户信息',templet:'#imgTemplet',width:200},
                {field: 'liveUserId', title: '主播ID',align:"center"},
                {field: 'userLevel', title: '等级',align:"center"},
                {field: 'totalIncome', title: '总收入',align:"center"},
                {field: 'onlineTime', title: '月在线时长（小时）',width:200,align:"center"},
                {field: 'auditTime', title: '签约时间',align:"center"},
                {field: 'totalCoin', title: '持有金币',width:150,align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center"}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("liveUserDetailInfoT","liveUserId",data.liveUserId)
            }
            else if(obj.event ==="toPage"){
                window.localStorage.setItem('displayName',data.displayName);
                jumpDetailsUrl("userBetRecord","userId",data.id)
            }else if(obj.event ==="unseal"){
                var arr =[]
                arr.push(data.liveUserId)
                layer.open({
                    content: "确认解封主播吗？"
                    ,btn: ['确认', '取消']
                    ,yes: function(index, layero){
                        $.post({
                            url:window.ioApi.liveUser.unLockLiveList,
                            data:JSON.stringify(arr),//传的是json数据
                            contentType: "application/json; charset=utf-8",//告知接收spring，接收json数据并转换
                            dataType:'json',
                            success:function (res) {
                                console.log(res);
                                location.reload();
                            }
                        })

                    }
                    ,btn3: function(index, layero){

                    }
                    ,cancel: function(){

                    }
                });


            }
        });

        roleTableBtn(["ALLBANLIVEUSERLISTTUNLOCKLIVELIST"],"unseal")
    });
}

$("#banUser").on("click",function () {
    var arr = []
    $('.laytable-cell-checkbox').each(function(i,item){
        if($(item).find('.layui-form-checked').length>0){
            if(tData[i-1] !== undefined && tData[i-1] !== ""){
                arr.push(tData[i-1].liveUserId)
            }
        }
    })
    console.log(arr);
    tips = "确认解封主播吗？"
    layer.open({
        content: tips
        ,btn: ['确认', '取消']
        ,yes: function(index, layero){

            $.post({
                url:window.ioApi.liveUser.unLockLiveList,
                data:JSON.stringify(arr),//传的是json数据
                contentType: "application/json; charset=utf-8",//告知接收spring，接收json数据并转换
                dataType:'json',
                success:function (res) {
                    console.log(res);
                    location.reload();
                }
            })

        }
        ,btn3: function(index, layero){

        }
        ,cancel: function(){

        }
    });
})

function getFormData() {
    var liveUserId = $("#element").val()
    var liveUserName = $("#class").val()
    var roomId = $("#room").val()
    var startTime = $("#test1").val()===""?"":new Date($("#test1").val()).getTime()
    var endTime =$("#test2").val()===""?"":new Date($("#test2").val()).getTime()
    var platform = 2
    return {
        liveUserId,
        liveUserName,
        roomId,
        platform,
        startTime,
        endTime
    }
}