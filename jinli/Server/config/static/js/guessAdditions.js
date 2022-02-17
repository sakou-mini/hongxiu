var send = {};
var upGuessImg;
var upWindowImg;
layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});

layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#showStartTime'
        ,type: 'datetime'
    });
});
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#showEndTime'
        ,type: 'datetime'
    });
});
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#wagerStartTime'
        ,type: 'datetime'
    });
});
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#wagerEndTime'
        ,type: 'datetime'
    });
});

layui.use('form', function(){
    var form = layui.form;
    form.on('select(itemSelect)', function(data){
        var itemSize = data.value;
        console.log("触发");
        $("#itemListBox").get(0).innerHTML = "";
        for(var i = 0;i<itemSize;i++){
            var itemNum = i+1;
            $("#itemListBox").get(0).innerHTML += '<input type="text" name="title" required  lay-verify="required" placeholder="选项'+itemNum+'" autocomplete="off" class="layui-input guessItem">';
        }
    });
});
intiUpLoad("upGuessImg",upGuessImgCallBack);
intiUpLoad("upWindowImg",upWindowImgCallBack);

function openLayer(imgSrc) {
    layer.open({
        title: '查看图片详情'
        ,content:"<img src='"+imgSrc+"' style='width: 700px' >",
        offset: '100px'
    });
}

$("#guessSort").on("change",function () {
    var sort = $("#guessSort").val();
    console.log("触发");
    if(sort < 1 ){
        $("#guessSort").val(1)
    }
    if(sort > 100){
        $("#guessSort").val(100)
    }
});

function upGuessImgCallBack(res) {
    upGuessImg = res;
    $("#ImgGuess").get(0).innerHTML ="";
    $("#ImgGuess").get(0).innerHTML +='<button style="margin-left: 40px;position: absolute;top: 10px;" class="layui-btn layui-btn-normal" id="resetUpGuessImgBtn" type="button">重新上传</button>'+
        '<img class="lookImg" style="width: 40%;margin-left: 150px" src="'+window.config.imgUrl+upGuessImg+'" data-value="'+window.config.imgUrl+upGuessImg+'" type="button">';
    $(".lookImg").on("click",function () {
        openLayer(this.getAttribute("data-value"));
    });
    $("#resetUpGuessImgBtn").on("click",function () {
        $("#ImgGuess").get(0).innerHTML ="";
        $("#ImgGuess").get(0).innerHTML +='<div id="upGuessImg"></div>';
        intiUpLoad("upGuessImg",upGuessImgCallBack);
    })
}
function upWindowImgCallBack(res) {
    upWindowImg =res;
    $("#ImgWin").get(0).innerHTML ="";
    $("#ImgWin").get(0).innerHTML +='<button style="margin-left: 40px;position: absolute;top: 10px;" class="layui-btn layui-btn-normal" id="resetUpWindowImgBtn" type="button">重新上传</button>'+
        '<img class="lookImg" style="width: 40%;margin-left: 150px" src="'+window.config.imgUrl+upWindowImg+'" data-value="'+window.config.imgUrl+upWindowImg+'" type="button">';
    $(".lookImg").on("click",function () {
        openLayer(this.getAttribute("data-value"));
    });
    $("#resetUpWindowImgBtn").on("click",function () {
        $("#ImgWin").get(0).innerHTML ="";
        $("#ImgWin").get(0).innerHTML +='<div id="upWindowImg"></div>';
        intiUpLoad("upWindowImg",upWindowImgCallBack);
    })
}

$("#upData").on("click",function () {
    updata()
});



function updata() {
    var itemList = [];
    var itemElList = document.getElementsByClassName("guessItem");
    for(var i=0;i<itemElList.length;i++){
        itemList.push(itemElList[i].value)
    }
    var title = $("#title").val();
    var subtitle = $("#subtitle").val();
    var windowImg = upWindowImg;
    var guessImg = upGuessImg;
    var guessType = $("#typeSelect").val();
    var showStartTime;
    var showEndTime;
    var wagerStartTime;
    var wagerEndTime;
    if(isIE()){
         showStartTime = new Date($("#showStartTime").val().replace(/-/g, '/')).getTime();
         showEndTime = new Date($("#showEndTime").val().replace(/-/g, '/')).getTime();
         wagerStartTime = new Date($("#wagerStartTime").val().replace(/-/g, '/')).getTime();
         wagerEndTime = new Date($("#wagerEndTime").val().replace(/-/g, '/')).getTime();
    }else {
        showStartTime = new Date($("#showStartTime").val()).getTime();
        showEndTime = new Date($("#showEndTime").val()).getTime();
        wagerStartTime = new Date($("#wagerStartTime").val()).getTime();
        wagerEndTime = new Date($("#wagerEndTime").val()).getTime();
    }

    var sort = $("#guessSort").val();
    send.itemList = itemList;
    send.title = title;
    send.subtitle = subtitle;
    send.windowImg = "/"+windowImg;
    send.guessImg = "/"+guessImg;
    send.guessType = parseInt(guessType);
    send.showStartTime = showStartTime;
    send.showEndTime = showEndTime;
    send.wagerStartTime = wagerStartTime;
    send.wagerEndTime=wagerEndTime;
    send.sort = parseInt(sort);
    var sendOk = upLoadVerification(send,0);
    console.log(send);
    console.log(sendOk);
    if(sendOk.code === 200){
        send.itemList = JSON.stringify(itemList);
        send.guessImg = send.guessImg.substr(1);
        send.windowImg = send.windowImg.substr(1);
        $.post({
            url:window.ioApi.guess.addNewGuess,
            data:send,
            success:function (res) {
                if (res.code == 200){
                    layer.confirm('上传成功',{
                        btn: ['确认'] ,
                        area: ['500px']
                    },function (index) {
                        window.history.back(-1)
                    });
                }else {
                    layer.confirm('任意时间不得小于服务器的当前时间',{
                        btn: ['确认'] ,
                        area: ['500px']
                    })
                }
            },
            error:function(xhr, exception){
                layer.open({
                    title: '错误信息'
                    ,content: '创建竞猜时，服务器出错，请联系管理员'
                });
            }
        })
    }
    else if(sendOk.code === 400){
        var nullKey;
        switch (sendOk.nullKey) {
            case "itemList":
                nullKey = "竞猜选项";
                break;
            case "title":
                nullKey = "竞猜标题";
                break;
            case "subtitle":
                nullKey = "竞猜副标题";
                break;
            case "windowImg":
                nullKey = "窗口图片";
                break;
            case "guessImg":
                nullKey = "竞猜页图片";
                break;
            case "guessType":
                nullKey = "竞猜类型";
                break;
            case "bet":
                nullKey = "单注金额";
                break;
            case "showStartTime":
                nullKey = "展示开始时间";
                break;
            case "showEndTime":
                nullKey = "展示结束时间";
                break;
            case "wagerStartTime":
                nullKey = "下注开始时间";
                break;
            case "wagerEndTime":
                nullKey = "下注结束时间";
                break;
            case "sort":
                nullKey = "排序";
                break
        }
        layer.open({
            title: '错误信息'
            ,content: '竞猜信息 "'+nullKey+'" 为空'
        });
    }
    else {
        layer.open({
            title: '错误信息'
            ,content: sendOk.msg
        });
    }
}


