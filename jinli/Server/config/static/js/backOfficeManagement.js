
layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});
$("#addBtn").on("click",function () {
    jumpUrl("backOfficeUserAdditions");
});

displayRoleElement(["ADDNEWBACKOFFICE"],"addBtn")

var page = 1;
var size = 10;
var count;

function tableDataList(list) {
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo',
            data: list,
            title: '管理员列表',
            height:500,
            loading:false,
            cols: [[
                {field: 'id', title: '管理员ID'},
                {field: 'accountName', title: '管理员账号'},
                {field: 'createDate', title: '管理员创建时间'},
                {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload',
            page: false
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("backOfficeUserEdit","id",data.id)
            }else if(obj.event === 'password'){

                var pwdFlag = false;
                var qpwdFlag = false;
                $("#pwd").on("keyup",function () {
                    if($("#pwd").val() === $("#qpwd").val()){
                        $("#qpwd").attr("style","width: 200px;");
                        $("#qpwdErrMsg").text("");
                        qpwdFlag = true
                    }
                    if($("#qpwd").val() !== "" && ($("#pwd").val() !== $("#qpwd").val())){
                        $("#qpwd").attr("style","box-shadow: 0 0 10px #ff0000;width: 200px;");
                        $("#qpwdErrMsg").text("两次密码不一致");
                        qpwdFlag = false
                    }
                    if($("#pwd").val().length < 6){
                        $("#pwd").attr("style","box-shadow: 0 0 10px #ff0000;width: 200px;");
                        $("#pwdErrMsg").text("密码长度大于6");
                        pwdFlag = false
                    }else {
                        $("#pwd").attr("style",";width: 200px;");
                        $("#pwdErrMsg").text("");
                        pwdFlag = true
                    }
                });

                $("#qpwd").focus(function(){
                    document.onkeyup = function () {
                        if(($("#pwd").val() !== $("#qpwd").val())&&$("#pwd").val() !==""){
                            $("#qpwd").attr("style","box-shadow: 0 0 10px #ff0000;width: 200px;");
                            $("#qpwdErrMsg").text("两次密码不一致");
                            qpwdFlag = false
                        }else {
                            $("#qpwd").attr("style","width: 200px;");
                            $("#qpwdErrMsg").text("");
                            qpwdFlag = true
                        }

                    }
                });
                $("#qpwd").blur(function () {
                    document.onkeyup = null
                });
                layer.open({
                    type:1,
                    area:['500px','300px'],
                    title: '修改密码'
                    ,content: $("#password"),
                    shade: 0,
                    btn: ['提交']
                    ,btn1: function(index, layero){
                        if(pwdFlag && qpwdFlag){
                            //管理员ID
                            // data.id

                            var pwd = $("#pwd").val();
                            $.post({
                                url:window.ioApi.backOffice.modifyPassword,
                                data:{
                                    backOfficeUserId:data.id,
                                    password:pwd,
                                },
                                success:function (res) {
                                    layer.closeAll();
                                    layer.open({
                                        title: '提示'
                                        ,content: '提交成功',
                                        btn: ['确定']
                                        ,btn1: function(index, layero){
                                            location.reload()
                                        },
                                        cancel: function(layero,index){
                                            layer.closeAll();
                                        }
                                    });
                                }
                            })
                        }
                    },
                    cancel: function(layero,index){
                        $("#qpwd").val("")
                        $("#pwd").val("")
                        layer.closeAll();
                    }

                });







            }
        });

        roleTableBtn(["EDITROLE"],"detail")
        roleTableBtn(["CHANGEBACKOFFICEPASSWORD"],"password")
    })
}

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
                        url: window.ioApi.backOffice.getBackOfficeUserList,
                        data: {
                            page:page,
                            size:size
                        },
                        success: function (res) {
                            console.log(res);
                            tableDataList(timeFilter(res.data));
                            count = res.count;
                            page = obj.curr;
                            getPage(res.count,obj.curr)
                        },
                        error:function () {
                            layer.open({
                                title: '错误信息'
                                ,content: '获取管理员列表失败，请联系管理员'
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
    $.get({
        url: window.ioApi.backOffice.getBackOfficeUserList,
        data: {
            page:page,
            size:size
        },
        success: function (res) {
            console.log(res);
            tableDataList(timeFilter(res.data));
            count = res.count;
            getPage()
        },
        error:function () {
            layer.open({
                title: '错误信息'
                ,content: '获取管理员列表失败，请联系管理员'
            });
        }
    })
}

function timeFilter(data) {
    for(var i=0;i<data.length;i++){
        data[i].createDate = formatDate(data[i].createDate)
    }
    return data
}