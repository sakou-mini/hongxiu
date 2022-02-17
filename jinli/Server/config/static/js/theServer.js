layui.use('element', function(){
    var element = layui.element;
});
layui.use('form', function() {
    var form = layui.form;
});
var isClose = false;
var getServerInfo;


displayRoleElement(["OPENCLOSESERVER"],"serverClose")

getServerInfo = setInterval(getServe,1000);
getServe();
function getServe(){
    $.get({
        url:window.ioApi.backOffice.getServerInfo,
        data:{},
        success:function (res) {
            var btn = $("#serverClose");
            switch (res.statue) {
                case "RUNNING":
                    res.statue = "运行中";
                    btn.text("关闭服务器");
                    btn.attr("class","layui-btn layui-btn-normal");
                    isClose = false;
                    break;
                case "STOP" :
                    res.statue = "已停止";
                    btn.text("开启服务器");
                    btn.attr("class","layui-btn layui-btn-danger");
                    isClose = true;
            }
            $("#liveCount").text(res.liveCount);
            $("#liveGameCount").text(res.liveGameCount);
            $("#onlineCount").text(res.onlineCount);
            $("#raceCount").text(res.raceCount);
            $("#statue").text(res.statue);
        }
    })
}

$("#serverClose").on("click",function () {
    if(!isClose){
        layer.confirm('关闭服务器警告！你即将在当前服务器中进行的游戏结束后关闭服务器，确定执行吗？',{
            btn: ['确认', '取消']
        },function (index) {
            $.get({
                url:window.ioApi.backOffice.serverClose,
                data:{},
                success:function (res) {
                    // location.reload();
                    layer.closeAll();
                    console.log(res);
                }
            })
        })
    }else {
        layer.confirm('是否启动服务器？',{
            btn: ['确认', '取消']
        },function (index) {
            $.get({
                url:window.ioApi.backOffice.serverOpen,
                data:{},
                success:function (res) {
                    // location.reload();
                    console.log(res);
                    layer.closeAll();
                }
            })
        })

    }
})