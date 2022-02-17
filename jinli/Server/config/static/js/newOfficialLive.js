gameTypeInit("hasBankerGame","noBankerGame","emptyBankerGame");
layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});

var patterns,gameId,banker;
layui.use('form', function(){
    var form = layui.form;
    form.on('select(game)', function(data){
        console.log(data);
        if(data.value === "1"){
            $("#noBankerGameBox").removeAttr("class","notShow");
            $("#hasBankerGameBox").attr("class","notShow");
            $("#emptyBankerGameBox").attr("class","notShow");
        }
        else if(data.value === "2"){
            $("#noBankerGameBox").attr("class","notShow");
            $("#hasBankerGameBox").removeAttr("class","notShow");
            $("#emptyBankerGameBox").attr("class","notShow");
        }
        else if(data.value === "3"){
            $("#noBankerGameBox").attr("class","notShow");
            $("#hasBankerGameBox").removeAttr("class","notShow");
            $("#emptyBankerGameBox").attr("class","notShow");
        }
        else {
            $("#noBankerGameBox").attr("class","notShow");
            $("#hasBankerGameBox").attr("class","notShow");
            $("#emptyBankerGameBox").removeAttr("class","notShow");
        }
    });
});
intiLiveUpLoad("liveUserImg",upLiveUserImg,1);
// intiLiveUpLoad("roomImg",upRoomImg,2);

function openLayer(imgSrc) {
    layer.open({
        title: '查看图片详情'
        ,content:"<img src='"+imgSrc+"' style='width: 700px' >",
        offset: '100px'
    });
}

var roomNameFlag = true;
$('#roomName').on('compositionstart',function(){
    roomNameFlag = false;
});

$('#roomName').on('compositionend',function(){
    roomNameFlag = true;
});


$("#roomName").on("input",function(e){
    //获取input输入的值
    console.log(e);
    setTimeout(function(){
        if(roomNameFlag){
            if(e.delegateTarget.value.length > 8){
                e.delegateTarget.value = e.delegateTarget.value.slice(0, 8);
                layer.confirm('直播间名最多8个字符',{
                    btn: ['确认'] ,
                    area: ['500px']
                },function (index) {
                    layer.closeAll();
                });

            }
        }
    },0)
});


var liveUserNameFlag = true;
$('#liveUserName').on('compositionstart',function(){
    liveUserNameFlag = false;
});

$('#liveUserName').on('compositionend',function(){
    liveUserNameFlag = true;
});

$("#liveUserName").on("input",function(e){
    //获取input输入的值
    setTimeout(function(){
        if(liveUserNameFlag){
            if(e.delegateTarget.value.length > 8){
                e.delegateTarget.value = e.delegateTarget.value.slice(0, 8);
                layer.confirm('主播名最多8个字符',{
                    btn: ['确认'] ,
                    area: ['500px']
                },function (index) {
                    layer.closeAll();
                });
            }
        }
    },0)
});

var liveUserImg;
var roomImg;
function upLiveUserImg(res) {
    liveUserImg = res[0];
    console.log(res);
    $("#liveUserImgBox").get(0).innerHTML ="";
    $("#liveUserImgBox").get(0).innerHTML +='<button style="margin-left: 40px;position: absolute;top: 10px;" class="layui-btn layui-btn-normal" id="resetLiveUserImgBtn" type="button">重新上传</button>'+
        '<img class="lookImg" style="width: 40%;margin-left: 150px" src="'+window.config.imgUrl+liveUserImg+'" data-value="'+window.config.imgUrl+liveUserImg+'" type="button">';
    $(".lookImg").on("click",function () {
        openLayer(this.getAttribute("data-value"));
    });
    $("#resetLiveUserImgBtn").on("click",function () {
        $("#liveUserImgBox").get(0).innerHTML ="";
        $("#liveUserImgBox").get(0).innerHTML +='<div id="liveUserImg"></div>';
        intiLiveUpLoad("liveUserImg",upLiveUserImg);
    })
}

function upRoomImg(res) {
    roomImg = res[0];
    $("#roomImgBox").get(0).innerHTML ="";
    $("#roomImgBox").get(0).innerHTML +='<button style="margin-left: 40px;position: absolute;top: 10px;" class="layui-btn layui-btn-normal" id="resetRoomImgBtn" type="button">重新上传</button>'+
        '<img class="lookImg" style="width: 40%;margin-left: 150px" src="'+window.config.imgUrl+roomImg+'" data-value="'+window.config.imgUrl+roomImg+'" type="button">';
    $(".lookImg").on("click",function () {
        openLayer(this.getAttribute("data-value"));
    });
    $("#resetRoomImgBtn").on("click",function () {
        $("#roomImgBox").get(0).innerHTML ="";
        $("#roomImgBox").get(0).innerHTML +='<div id="roomImg"><label type="button" class="xdg_LoadImgBtn" id="roomImgBtn"><span>选择图片</span><input type="file" id="imgFile"></label></div>';
        info()
    })
}

$("#upData").on("click",upData);

function upData() {
    var roomName = $("#roomName").val();
    var liveUserName = $("#liveUserName").val();
    var roomDisplayId = $("#roomDisplayId").val();
    patterns = parseInt($("#game").val());
    switch (patterns) {
        case 1:
            gameId = parseInt($("#noBankerGame").val());
            break;
        case 2:
            gameId = parseInt($("#hasBankerGame").val());
            banker = true;
            break;
        case 3:
            gameId = parseInt($("#hasBankerGame").val());
            banker = false;
            break;
        case 4:
            gameId = parseInt($("#emptyBankerGame").val());
            banker = false;
    }
    var verification =  officialVerification({
        roomName:roomName,
        liveUserName:liveUserName,
        roomDisplayId:roomDisplayId,
        liveUserImg:liveUserImg,
        roomImg:roomImg
        });
    if(verification.code!==200){
        layer.confirm(verification.msg,{
            btn: ['确认'] ,
            area: ['500px']
        },function (index) {
            layer.closeAll();
        });
    }
    else {
        var log ={roomName:roomName,
            liveUserName:liveUserName,
            roomDisplayId:roomDisplayId,
            gameId:gameId,
            banker:banker,
            liveUserImg:liveUserImg,
            roomImg:roomImg}
        console.log(log);
        $.get({
            url:window.ioApi.officialLive.addOfficialLive,
            data:{
                roomName:roomName,
                liveUserName:liveUserName,
                roomDisplayId:roomDisplayId,
                gameId:gameId,
                banker:banker,
                liveUserImg:liveUserImg,
                roomImg:roomImg
            },
            success:function (res) {
                if(res === "success"){
                    layer.confirm('上传成功',{
                        btn: ['确认'] ,
                        area: ['500px']
                    },function (index) {
                        window.history.back(-1)
                    });
                }else if(res === "liveUser"){
                    layer.confirm('主播名字重复',{
                        btn: ['确认'] ,
                        area: ['500px']
                    },function (index) {
                        layer.closeAll();
                    });
                }else if(res === "roomDisplayId"){
                    layer.confirm('直播间号重复',{
                        btn: ['确认'] ,
                        area: ['500px']
                    },function (index) {
                        layer.closeAll();
                    });
                }else if(res === "roomName"){
                    layer.confirm("直播间名字重复",{
                        btn:["确认"],
                        area:["500px"]
                    },function (index) {
                        layer.closeAll();
                    })
                }
            }
        })
    }

}

