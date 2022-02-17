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
                        url:window.ioApi.user.queryBanUserList,
                        data:searchData,
                        success:function (res) {
                            dataCount = res.count;
                            console.log(userFilter(res.data));
                            tableData(userFilter(res.data));
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
    searchData.page = 1
    searchData.size = size
    $.get({
        url:window.ioApi.user.queryBanUserList,
        data:searchData,
        success:function (res) {
            dataCount = res.count;
            tableData(userFilter(res.data));
            pageGat(res.count,nowPage);
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
        url:window.ioApi.user.queryBanUserList,
        data:searchData,
        success:function (res) {
            if(res.code === 1000){
                layer.msg("你搜索的用户是主播")
                tableData([]);
            }else {
                dataCount = res.count;
                tableData(userFilter(res.data));
                pageGat(res.count,nowPage)
            }

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
                {field: 'phoneNumber', title: '手机号',align:"center"},
                {field: 'vipType', title: '贵族等级',width:120,align:"center"},
                {field: 'gameCoin', title: '余额',width:130,align:"center"},
                {field: 'recharge', title: '累计充值',width:100,align:"center"},
                {field: 'lastLoginTime', title: '上次登录时间',align:"center"},
                {field: 'lastIp', title: '用户IP',width:150,align:"center"},
                {field: 'platformTag', title: '平台',align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center",width:300}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("userDetails","userId",data.id)
            }
            else if(obj.event ==="toPage"){
                window.localStorage.setItem('displayName',data.displayName);
                jumpDetailsUrl("userBetRecord","userId",data.id)
            }else if(obj.event ==="unseal"){
                var arr =[]
                arr.push(data.id)
                layer.open({
                    content: "确认解封用户吗？"
                    ,btn: ['确认', '取消']
                    ,yes: function(index, layero){
                        $.post({
                            url:window.ioApi.user.unsealedUser,
                            data:JSON.stringify(arr),//传的是json数据
                            contentType: "application/json; charset=utf-8",//告知接收spring，接收json数据并转换
                            dataType:'json',
                            success:function (res) {
                                console.log(res);
                                location.reload();
                            },
                            error:function (res) {
                                if(res.status === 403){
                                    layer.msg("你没有权限")
                                }
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
        roleTableBtn(["BANUSERLISTUSERDETAILS"],"detail")
        roleTableBtn(["BANUSERUSERBETRECORD"],"toPage")
        roleTableBtn(["UNSEALEDUSER"],"unseal")
    });
}

$("#banUser").on("click",function () {
    var arr = []
    $('.laytable-cell-checkbox').each(function(i,item){
        if($(item).find('.layui-form-checked').length>0){
            if(tData[i-1] !== undefined && tData[i-1] !== ""){
                arr.push(tData[i-1].id)
            }
        }
    })
    tips = "确认解封用户吗？"
    layer.open({
        content: tips
        ,btn: ['确认', '取消']
        ,yes: function(index, layero){

                $.post({
                    url:window.ioApi.user.unsealedUser,
                    data:JSON.stringify(arr),//传的是json数据
                    contentType: "application/json; charset=utf-8",//告知接收spring，接收json数据并转换
                    dataType:'json',
                    success:function (res) {
                        console.log(res);
                        location.reload();
                    },
                    error:function (res) {
                        if(res.status === 403){
                            layer.msg("你没有权限")
                        }
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
    var userId = $("#element").val().trim();
    var phoneNumber = $("#class").val().trim();
    var ip = $("#ip").val().trim();
    var startTime = $("#test1").val()===""?"":new Date($("#test1").val()).getTime()
    var endTime =$("#test2").val()===""?"":new Date($("#test2").val()).getTime()
    var platform = 1
    return {
        userId,
        phoneNumber,
        ip,
        platform,
        startTime,
        endTime
    }
}