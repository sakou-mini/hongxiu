layui.use('form', function(){
    var form = layui.form;
    form.on('select(itemSelect)', function(data){

    });
});

$("#searchBtn").on("click",function () {
    $.get({
        url:window.ioApi.liveUser.getRoomList,
        data:{
            liveUserId:$("#liveUserId").val(),
            displayName:$("#displayName").val(),
            roomId:$("#roomId").val(),
            platform:3
        },
        success:function (res) {
            if(res.length===0){
                noDataBox()
            }else {
                initLiveBox(res)
            }
        }
    });
})

getRoomData()

function getRoomData(){
    $.get({
        url:window.ioApi.liveUser.getRoomList,
        data:{
            liveUserId:"",
            displayName:"",
            roomId:"",
            platform:3
        },
        success:function (res) {
            console.log(res);
            if(res.length===0){
                noDataBox()
            }else {
                initLiveBox(res)
            }
        }
    });

}




function initLiveBox(res) {
    $("#liveBox").get(0).innerHTML=""
    for(var i=0;i<res.length;i++){
        console.log(res[i]);
        if(res[i].roomImage===""){
            res[i].roomImage="/config/static/img/default_roomImg.jpg"
        }else {
            res[i].roomImage=window.config.imgUrl+ res[i].roomImage
        }
        res[i].avatarUrl=window.config.imgUrl+  res[i].avatarUrl;
        var isHot
        if(res[i].hot){
            isHot = "layui-bg-orange"
        }else {
            isHot ="layui-bg-gray"
        }
        var sharedPlatform = ""
        if(res[i].sharedPlatform.length<3){
            for(let x=0;x<res[i].sharedPlatform.length;x++){
                sharedPlatform +=getPlatform(res[i].sharedPlatform[x])+"、"
            }
            sharedPlatform = sharedPlatform.substr(0, sharedPlatform.length - 1);
        }else {
            sharedPlatform = "全部"
        }
        var livePattern = res[i].livePattern
        switch (livePattern) {
            case "LIVE_AUDIO":
                livePattern = "音频"
                break
            case "LIVE_VIDEO":
                livePattern = "视频"
                break
        }
        var isRecommend;
        var displayRecommend;
        var weight
        $("#liveBox").get(0).innerHTML+=`
                <div class="flx_item" data-value="${res[i].roomId}">
                    <div style="width: 100%;height: 75%; position: relative">
                        <img style="width: 100%;height: 100%;" src="${res[i].roomImage}"/>
                        <div class="liveRecommendBtn">
                            <div class="icl" data-value="${res[i].roomId}">+</div>
                            <div class="icr" data-value="${res[i].roomId}">-</div>
                        </div>
                    </div>
                    <div style="width: 100%;height: 25%;overflow: hidden">
                        <div style="height: 100%;float: left;display: flex; flex-direction:column;justify-content:center;">
                            <img  style="height: 46px;width: 46px;line-height: 100%;margin-left: 10px;border-radius: 100%" src="${res[i].avatarUrl}" />
                        </div>
                        <div style="float: left;height: 100%;margin-left: 20px;">
                             <div style="height: 50%; line-height: 200%">${res[i].displayName}   <span class="layui-badge ${isHot}">热</span>  </div>
                             <div style="overflow: hidden;height: 50%;font-size: 8px;line-height: 200%;">
                             <div style="float:left;"><span>直播间人数:</span><span>${res[i].audienceNum}</span></div>
                            </div>
                        </div>
                    </div>
                    <p class="flx_tag_pos">${gameTypeFormat(res[i].gameType)}</p>
                    <p class="flx_title_pos">${res[i].roomTitle}</p>
                    <p class="flx_platform_pos">${sharedPlatform}</p>
                    <p class="flx_livePattern_pos">${livePattern}</p>
                </div>
            `;
        $(".flx_item").on("click",function () {
            jumpDetailsUrl("liveDetailsQ","roomId",this.getAttribute("data-value"))
        });
        $(".icl").on("click",function (event) {
            $.get({
                url:window.ioApi.liveUser.swapRoomRecommendSort,
                data:{
                    swapRoomId:this.getAttribute("data-value"),
                    raiseRecommend:true,
                    platform:3
                },
                success:function (res) {
                    console.log(res);
                    if(res.code === 200){
                        getRoomData()
                    }
                }
            });

            event.stopPropagation();
        });
        $(".icr").on("click",function (event) {
            $.get({
                url:window.ioApi.liveUser.swapRoomRecommendSort,
                data:{
                    swapRoomId:this.getAttribute("data-value"),
                    raiseRecommend:false,
                    platform:3
                },
                success:function (res) {
                    console.log(res);
                    if(res.code === 200){
                        getRoomData()
                    }
                }
            });
            event.stopPropagation();
        })
    }
}

function noDataBox() {
    $("#liveBox").get(0).innerHTML="<h1 class='noDataBox'>暂无数据</h1>"
}