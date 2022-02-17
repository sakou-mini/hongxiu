var pc_exportData ;
var dp_exportData ;
var dl_exportData ;
var time1 = new Date(fun_date(-6)).getTime();
var time2 = new Date(fun_date(0)).getTime();


getInfoData();
function getInfoData() {
    $.get({
        url:window.ioApi.user.getMobileDevicesChartData,
        data:{
            startTime:time1,
            endTime:time2
        },
        success:function (res) {
            console.log(res);
            if(res.data.length === 0){
                dataRendering();
                return
                // layer.open({
                //     title: '提示'
                //     ,content: '这个时间段内没有数据'
                // });
            }
            var data = [];
            for(var i = 0;i<res.data.length;i++ ){
                var dataObj = {};
                dataObj.date = dateFormat("YYYY-mm-dd",res.data[i].date);
                dataObj.newUserBrand = res.data[i].newUserBrand;
                dataObj.activeUserBrand = res.data[i].activeUserBrand;
                dataObj.payingUserBrand = res.data[i].payingUserBrand;
                dataObj.payingUserOS = res.data[i].payingUserOS;
                data.push(dataObj);
            }
            if(data.length<2){
                data = timeAscendingOrder(data,"date");
            }else {
                data = dateDataInfo(timeAscendingOrder(data,"date"));
            }
            var nArr=[];
            var aArr=[];
            var bArr=[];
            var oArr=[];
            for(var x = 0;x<data.length;x++){
                console.log(mapInfo(data[x].newUserBrand));
                nArr.push(mapInfo(data[x].newUserBrand));
                aArr.push(mapInfo(data[x].activeUserBrand));
                bArr.push(mapInfo(data[x].payingUserBrand));
                for(var key in data[x].payingUserOS){
                    var obj ={os:null,value:null};
                    obj.os = key;
                    obj.value = data[x].payingUserOS[key];
                    oArr.push(obj)
                }
            }
            chartData.newUserBrand = dataEqualityCalculation(nArr);
            chartData.activeUserBrand = dataEqualityCalculation(aArr);
            chartData.payingUserBrand = dataEqualityCalculation(bArr);
            chartData.payingUserOS = osEqualityCalculation(oArr);
            dataRendering();
        }
    })
}


function dataEqualityCalculation(obj) {
    var arr =[]
    var BRAND_OTHER = {brand:"BRAND_OTHER",value:0};
    var BRAND_IPHONE = {brand:"BRAND_IPHONE",value:0};
    var BRAND_HUAWEI = {brand:"BRAND_HUAWEI",value:0};
    var BRAND_OPPO = {brand:"BRAND_OPPO",value:0};
    var BRAND_VIVO = {brand:"BRAND_VIVO",value:0};
    var BRAND_XIAOMI = {brand:"BRAND_XIAOMI",value:0};
    var BRAND_MEIZU = {brand:"BRAND_MEIZU",value:0};
    var BRAND_SAMSUNG = {brand:"BRAND_SAMSUNG",value:0};
    var BRAND_ONEPLUS = {brand:"BRAND_ONEPLUS",value:0};
    var BRAND_REALME = {brand:"BRAND_REALME",value:0};
    for(var x=0 ;x<obj.length ;x++){
        for(var y =0;y<obj[x].length;y++){
            if(obj[x][y].brand === "BRAND_OTHER"){
                BRAND_OTHER.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_IPHONE"){
                BRAND_IPHONE.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_HUAWEI"){
                BRAND_HUAWEI.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_OPPO"){
                BRAND_OPPO.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_VIVO"){
                BRAND_VIVO.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_XIAOMI"){
                BRAND_XIAOMI.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_MEIZU"){
                BRAND_MEIZU.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_SAMSUNG"){
                BRAND_SAMSUNG.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_ONEPLUS"){
                BRAND_ONEPLUS.value+=obj[x][y].value
            }else if(obj[x][y].brand === "BRAND_REALME"){
                BRAND_REALME.value+=obj[x][y].value
            }
        }
    }
    arr.push(BRAND_OTHER);
    arr.push(BRAND_IPHONE);
    arr.push(BRAND_HUAWEI);
    arr.push(BRAND_OPPO);
    arr.push(BRAND_VIVO);
    arr.push(BRAND_XIAOMI);
    arr.push(BRAND_MEIZU);
    arr.push(BRAND_SAMSUNG);
    arr.push(BRAND_ONEPLUS);
    arr.push(BRAND_REALME);
    console.log(arr);
    return arr;
}

function osEqualityCalculation(obj) {
    var arr =[]
    var OS_OTHER = {os:"OS_OTHER",value:0};
    var OS_ANDROID = {os:"OS_ANDROID",value:0};
    var OS_IOS = {os:"OS_IOS",value:0};
    for(var x=0 ;x<obj.length ;x++){
        if(obj[x].os === "OS_OTHER"){
            OS_OTHER.value+=obj[x].value
        }else if(obj[x].os === "OS_ANDROID"){
            OS_ANDROID.value+=obj[x].value
        }else if(obj[x].os === "OS_IOS"){
            OS_IOS.value+=obj[x].value
        }
    }
    arr.push(OS_OTHER);
    arr.push(OS_ANDROID);
    arr.push(OS_IOS);
    return arr;
}



function mapInfo(arr) {
    var nArr = [];
    for(var key in arr){
        var obj ={brand:null,value:null};
        obj.brand = key;
        obj.value = arr[key];
        nArr.push(obj)
    }
    return nArr;
}

function dateDataInfo(data) {
    var dateArr = [];
    for(var x=0;x<data.length;x++){
        dateArr.push(data[x].date)
    }
    var rt = dateMakeUp(dateArr);
    if(rt === "success"){
        return data
    }else {
        for(var i=0;i<data.length;i++){
            if(data[i].date === rt){
                data.splice(i,0,{date:getNowFormatDate(data[i].date,1,"-"),
                    newUserBrand:{
                        BRAND_OTHER:0,
                        BRAND_IPHONE:0,
                        BRAND_HUAWEI:0,
                        BRAND_OPPO:0,
                        BRAND_VIVO:0,
                        BRAND_XIAOMI:0,
                        BRAND_MEIZU:0,
                        BRAND_SAMSUNG:0,
                        BRAND_ONEPLUS:0,
                        BRAND_REALME:0
                    },
                    activeUserBrand:{
                        BRAND_OTHER:0,
                        BRAND_IPHONE:0,
                        BRAND_HUAWEI:0,
                        BRAND_OPPO:0,
                        BRAND_VIVO:0,
                        BRAND_XIAOMI:0,
                        BRAND_MEIZU:0,
                        BRAND_SAMSUNG:0,
                        BRAND_ONEPLUS:0,
                        BRAND_REALME:0
                    },
                    payingUserBrand:{
                        BRAND_OTHER:0,
                        BRAND_IPHONE:0,
                        BRAND_HUAWEI:0,
                        BRAND_OPPO:0,
                        BRAND_VIVO:0,
                        BRAND_XIAOMI:0,
                        BRAND_MEIZU:0,
                        BRAND_SAMSUNG:0,
                        BRAND_ONEPLUS:0,
                        BRAND_REALME:0
                    },
                    payingUserOS:{
                        OS_OTHER:0,OS_ANDROID:0,OS_IOS:0
                    }});
                dateDataInfo(data)
            }
        }
    }
}

// 时间选择器初始化
function timeUpdate(){
    layui.use('laydate', function(){
        var laydate = layui.laydate;
        laydate.render({
            elem: '#theT_openTime',
            value:fun_date(-6),
            max:0
        })
        laydate.render({
            elem: '#theT_endTime',
            value:fun_date(0),
            max:0
        });
    });
}

//定义下载内容

var chartData ={
    newUserBrand:[
        {brand:"BRAND_OTHER",value:35},
        {brand:"BRAND_IPHONE",value:452},
        {brand:"BRAND_HUAWEI",value:786},
        {brand:"BRAND_OPPO",value:824},
        {brand:"BRAND_VIVO",value:754},
        {brand:"BRAND_XIAOMI",value:425},
        {brand:"BRAND_MEIZU",value:488},
        {brand:"BRAND_SAMSUNG",value:242},
        {brand:"BRAND_ONEPLUS",value:422},
        {brand:"BRAND_REALME",value:234},
        ],
    activeUserBrand:[
        {brand:"BRAND_OTHER",value:14},
        {brand:"BRAND_IPHONE",value:485},
        {brand:"BRAND_HUAWEI",value:850},
        {brand:"BRAND_OPPO",value:542},
        {brand:"BRAND_VIVO",value:354},
        {brand:"BRAND_XIAOMI",value:345},
        {brand:"BRAND_MEIZU",value:248},
        {brand:"BRAND_SAMSUNG",value:845},
        {brand:"BRAND_ONEPLUS",value:452},
        {brand:"BRAND_REALME",value:324},
    ],
    payingUserBrand:[
        {brand:"BRAND_OTHER",value:354},
        {brand:"BRAND_IPHONE",value:535},
        {brand:"BRAND_HUAWEI",value:284},
        {brand:"BRAND_OPPO",value:534},
        {brand:"BRAND_VIVO",value:613},
        {brand:"BRAND_XIAOMI",value:746},
        {brand:"BRAND_MEIZU",value:234},
        {brand:"BRAND_SAMSUNG",value:534},
        {brand:"BRAND_ONEPLUS",value:123},
        {brand:"BRAND_REALME",value:431},
    ],
    payingUserResolution:[
        {pixel:"PIXEL_OTHER",value:23},
        {pixel:"PIXEL_HD",value:523},
        {pixel:"PIXEL_FHD",value:654},
        {pixel:"PIXEL_UFD",value:234},
    ],
    payingUserOS:[
        {os:"OS_OTHER",value:3},
        {os:"OS_ANDROID",value:6523},
        {os:"OS_IOS",value:234},
    ],
}

function dl_download() {
    var aoa = [
        ['操作系统', '付费玩家',"百分比"],
    ];
    for(var i = 0;i<dl_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dl_exportData.yAxis.data[i]);
        newDataArr.push(dl_exportData.series[0].data[i]);
        newDataArr.push(dl_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '操作系统.xlsx');
}

function dp_download() {
    var aoa = [
        ['分辨率', '付费玩家',"百分比"],
    ];
    for(var i = 0;i<dp_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dp_exportData.yAxis.data[i]);
        newDataArr.push(dp_exportData.series[0].data[i]);
        newDataArr.push(dp_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '分辨率.xlsx');
}

function pc_download(){
    if(pc_pageControl === "newPaidPlayers"){
        var aoa = [
            ['机型', '新增玩家',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '新增玩家机型.xlsx');
    }
    else if(pc_pageControl === "cumulativePaidPlayers"){
        var aoa = [
            ['机型', '活跃玩家',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '活跃玩家机型.xlsx');
    }
    else {
        var aoa = [
            ['机型', '付费玩家',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '付费玩家机型.xlsx');
    }

}


timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function () {
    if(verificationTheTTime() === "ok"){
        if(isIE()){
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        }else {
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
        }
        theTclose();
        innerTime();
        getInfoData()
    }
})

$("#theT_t").on("click",function () {
    $("#theT_openTime").val(fun_date(0));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_y").on("click",function () {
    $("#theT_openTime").val(fun_date(-1));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_w").on("click",function () {
    $("#theT_openTime").val(fun_date(-6));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})
$("#theT_m").on("click",function () {
    $("#theT_openTime").val(fun_date(-30));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
    getInfoData()
})

// 刷新时间选择器显示时间
function innerTime(){
    var day1 = formatSpecificDate(new Date($("#theT_openTime").val()));
    var day2 = formatSpecificDate(new Date($("#theT_endTime").val()));
    $("#theT_open").val(day1+" ~ "+day2);
}

//初始化时间选择器时间
$("#theT_open").val(fun_date(-6)+" ~ "+fun_date(0));

//时间控制器取消按钮
$("#theT_cancel").on("click",function () {
    theTclose();
});

//时间选择器显示方法
function theTshow() {
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose(){
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden theT_hidden")
}

var option;
//帮助弹框控件
var pc_helpControl = "hidden";
var dp_helpControl = "hidden";
var dl_helpControl = "hidden";
//页面控件
var pc_pageControl = "newPaidPlayers";

var pcChart;
var dpChart;
var dlChart;
//图表配置
option = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis',

        //百分比配置
        axisPointer: {
            label:{
                formatter: null
            }
        }
    },
    legend: {
        data: [
            // '新玩家', '老玩家'
        ]
    },
    grid: {
        left: '5%',
        right: '6%',
        bottom: '5%',
        containLabel: true
    },
    xAxis: {
        type: 'value',
        data: [
            // '2020-3-4', '2020-3-5', '2020-3-6', '2020-3-7', '2020-3-8', '2020-3-9', '2020-3-10'
        ]
    },

    yAxis: {
        type: 'category',
        //y坐标百分比配置
        axisLabel: {
            formatter:null
        }
    },
    series: [
        // {
        //     name: '老玩家',
        //     type: 'line',
        //     data: [312, 250, 351, 334, 299, 355, 381]
        // },
        // {
        //     name: '新玩家',
        //     type: 'line',
        //     data: [220, 182, 191, 234, 290, 330, 310]
        // }
    ]
};

function dataRendering(){
    pc_dataRendering();
    dp_dataRendering();
    dl_dataRendering();
};
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    if(pc_pageControl === "newPaidPlayers"){
        //    请求事件
        var simulationData = dataFiltering(chartData).newUserBrand;
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].brand);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%";
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "新增玩家机型";
        o.yAxis.data=time;
        o.legend.data = ["新增玩家"];
        o.series = [{
            name: '新增玩家',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "cumulativePaidPlayers") {
        var simulationData = dataFiltering(chartData).activeUserBrand;
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].brand);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%";
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "活跃玩家机型";
        o.yAxis.data=time;
        o.legend.data = ["活跃玩家"];
        o.series = [{
            name: '活跃玩家',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o);
    }else {
        var simulationData = dataFiltering(chartData).payingUserBrand;
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].brand);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%";
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "付费玩家机型";
        o.yAxis.data=time;
        o.legend.data = ["付费玩家"];
        o.series = [{
            name: '付费玩家',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }

    $("#pc_help").on("click",function () {
        if(pc_helpControl === "show"){
            $("#pc_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            pc_helpControl = "hidden";
        }
        else {
            $("#pc_helpTypeModality").attr("class","helpTypeModality");
            pc_helpControl = "show";
        }
    });
    $("#pc_download").on("click",function () {
        pc_download()
    })

}
function dp_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = dataFiltering(chartData).payingUserResolution;
    var time = [];
    var value = [];
    var num = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].pixel);
        value.unshift(simulationData[i].value);
        num += simulationData[i].value
    }
    var pointArr = [];

    for(var x = 0;x<value.length;x++){
        var percentage = ((value[x]/num)*100).toFixed(2)+"%";
        var pointObj = {
            coord:  [value[x],time[x]],
            value:percentage
        }
        pointArr.push(pointObj)
    }

    o.title.text = "分辨率";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    dp_exportData = JSON.parse(JSON.stringify(o));
    dp_HeavyLoad(o);

    $("#dp_help").on("click",function () {
        if(dp_helpControl === "show"){
            $("#dp_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            dp_helpControl = "hidden";
        }
        else {
            $("#dp_helpTypeModality").attr("class","helpTypeModality");
            dp_helpControl = "show";
        }
    });
    $("#dp_download").on("click",function () {
        dp_download()
    })
}
function dl_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = dataFiltering(chartData).payingUserOS;
    var time = [];
    var value = [];
    var num = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].os);
        value.unshift(simulationData[i].value);
        num += simulationData[i].value
    }
    var pointArr = [];
    for(var x = 0;x<value.length;x++){
        var percentage = ((value[x]/num)*100).toFixed(2)+"%"
        var pointObj = {
            coord: [value[x],time[x]],
            value:percentage
        }
        pointArr.push(pointObj)
    }

    o.title.text = "操作系统";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    dl_exportData = JSON.parse(JSON.stringify(o));
    dl_HeavyLoad(o);

    $("#dl_help").on("click",function () {
        if(dl_helpControl === "show"){
            $("#dl_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            dl_helpControl = "hidden";
        }
        else {
            $("#dl_helpTypeModality").attr("class","helpTypeModality");
            dl_helpControl = "show";
        }
    });
    $("#dl_download").on("click",function () {
        dl_download()
    })
}

//定义点击标签事件
$(".pc_titleClick").on("click",function () {
    pc_pageControl = this.getAttribute("data-value");
    pc_dataRendering()
});

//图表自适应
window.addEventListener("resize", function () {
    pcChart.resize();
    dpChart.resize();
    dlChart.resize();
});


//图表重载
function pc_HeavyLoad(o) {
    var pcChartDom = document.getElementById('pc_chart');
    pcChartDom.remove();
    $("#pc").get(0).innerHTML += '<div id="pc_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var pcChartDom = document.getElementById('pc_chart');
    pcChart = echarts.init(pcChartDom);
    pcChart.setOption(o);
}
function dp_HeavyLoad(o) {
    var dpChartDom = document.getElementById('dp_chart');
    dpChartDom.remove();
    $("#dp").get(0).innerHTML += '<div id="dp_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var dpChartDom = document.getElementById('dp_chart');
    dpChart = echarts.init(dpChartDom);
    dpChart.setOption(o);
}
function dl_HeavyLoad(o) {
    var dlChartDom = document.getElementById('dl_chart');
    dlChartDom.remove();
    $("#dl").get(0).innerHTML += '<div id="dl_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var dlChartDom = document.getElementById('dl_chart');
    dlChart = echarts.init(dlChartDom);
    dlChart.setOption(o);
}


function dataFiltering(d) {
    var data = JSON.parse(JSON.stringify(d));
    for(var i = 0;i<data.newUserBrand.length;i++){
        data.newUserBrand[i].brand = enumFilter(data.newUserBrand[i].brand);
        data.activeUserBrand[i].brand = enumFilter(data.activeUserBrand[i].brand);
        data.payingUserBrand[i].brand = enumFilter(data.payingUserBrand[i].brand);
    }
    for(var x=0;x<data.payingUserResolution.length;x++){
        data.payingUserResolution[x].pixel = enumFilter(data.payingUserResolution[x].pixel);
    }
    for(var y=0;y<data.payingUserOS.length;y++){
        data.payingUserOS[y].os = enumFilter(data.payingUserOS[y].os);
    }
    return data;
}

function enumFilter(brand) {
    switch (brand) {
        case "BRAND_OTHER":
            return "其他";
        case "BRAND_IPHONE":
            return "苹果";
        case "BRAND_HUAWEI":
            return "华为";
        case "BRAND_OPPO":
            return "OPPO";
        case "BRAND_VIVO":
            return "VIVO";
        case "BRAND_XIAOMI":
            return "小米";
        case "BRAND_MEIZU":
            return "魅族";
        case "BRAND_SAMSUNG":
            return "三星";
        case "BRAND_ONEPLUS":
            return "一加";
        case "BRAND_REALME":
            return "真我";
        case "PIXEL_OTHER":
            return "其他";
        case "PIXEL_HD":
            return "720*1280";
        case "PIXEL_FHD":
            return "1080*1920";
        case "PIXEL_UFD":
            return "1440*2560";
        case "OS_OTHER":
            return "其他";
        case "OS_ANDROID":
            return "android";
        case "OS_IOS":
            return "ios";
    }
}