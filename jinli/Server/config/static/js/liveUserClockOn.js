layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1',
        value:fun_date(0)
    });
});



init()

function init() {
    var limitDate = new Date(fun_date(0)+" 00:00:00").getTime()
    var platform = parseInt($("#platform").val())
    getDate({limitDate,platform})
}
var exportData

function getDate(data) {
    $.get({
        url:window.ioApi.liveUser.requestLiveLimitList,
        data:data,
        success:function (res) {
            console.log(res);
            exportData = res
            initTimeLine(res.clockWhiteList)
            $("#title").text(dateFormat("YYYY-mm-dd",res.limitDate))
        }
    })
}

$("#searchBtn").on("click",function () {
    var limitDate = new Date($("#test1").val()+" 00:00:00").getTime()
    var platform = parseInt($("#platform").val())
    getDate({limitDate,platform})
})



function initTimeLine(obj) {

    document.getElementById("dataList").innerHTML=""
    if(isNoDataObj(obj)){
        document.getElementById("dataList").innerHTML=`<h2 style="text-align: center">没有数据</h2>`
    }
    for(var key in obj){
        var liveUserData = ""
        for(var x=0;x<obj[key].length;x++){
            liveUserData +=obj[key][x].displayName+"&nbsp;&nbsp;&nbsp;("+obj[key][x].liveUserId+")"
            liveUserData+="<br>"
        }
        document.getElementById("dataList").innerHTML += `
         <li class="layui-timeline-item">
                                <i class="layui-icon layui-timeline-axis"></i>
                                <div class="layui-timeline-content layui-text">
                                    <h3 class="layui-timeline-title">${key}</h3>
                                    <p>
                                        ${liveUserData}
                                    </p>
                                </div>
                            </li>
        `

    }
}

function isNoDataObj(obj) {
    for(var key in obj) {
        return false;
    }
    return true;
}


$("#excel").on("click",function () {
    var data = exportData.clockWhiteList
    var aoa = [
        ['时间段', '主播1',"主播1",
            "主播2","主播3","主播4",
            "主播5","主播6","主播7",
            "主播8","主播9",
            "主播10", "主播11", "主播12",
            "主播13",
            "主播14", "主播15",
        ],
    ];
    for(var key in data){
        var newDataArr = [];
        newDataArr.push(key);
        for(var i =0 ;i<data[key].length;i++){
            newDataArr.push(data[key][i].displayName+"("+data[key][i].liveUserId+")")
        }
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), dateFormat("YYYY-mm-dd",exportData.limitDate)+' 开播列表 导出日期：'+dateFormat("YYYY-mm-dd",new Date().getTime())+'.xlsx');
})