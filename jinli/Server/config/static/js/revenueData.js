var pc_exportData ;
var dp_exportData ;
var dl_exportData ;
var time1 = new Date(fun_date(-6)).getTime();
var time2 = new Date(fun_date(0)).getTime();



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

function dl_download() {
    var aoa = [
        ['渠道', '金额',"百分比"],
    ];
    for(var i = 0;i<dl_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dl_exportData.yAxis.data[i]);
        newDataArr.push(dl_exportData.series[0].data[i]);
        newDataArr.push(dl_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '各渠道收入.xlsx');
}

function dp_download() {
    var aoa = [
        ['地区', '金额',"百分比"],
    ];
    for(var i = 0;i<dp_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dp_exportData.yAxis.data[i]);
        newDataArr.push(dp_exportData.series[0].data[i]);
        newDataArr.push(dp_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '各地区收入.xlsx');
}

function pc_download(){
    if(pc_pageControl === "incomeAmount"){
        var aoa = [
            ['日期', '收入金额',],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '收入金额.xlsx');
    }
    else if(pc_pageControl === "rechargeTimes"){
        var aoa = [
            ['日期', '充值次数'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '充值次数.xlsx');
    }
    else {
        var aoa = [
            ['日期', '充值人数',],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '充值人数.xlsx');
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
var pc_pageControl = "incomeAmount";

var pcChart;
var dpChart;
var dlChart;
//图表配置
option = {
    title: {
        text: ''
    },

    toolbox:{
        feature:{
            dataView:{}
        }
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
        type: 'category',
        data: [
            // '2020-3-4', '2020-3-5', '2020-3-6', '2020-3-7', '2020-3-8', '2020-3-9', '2020-3-10'
        ]
    },

    yAxis: {
        type: 'value',
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
dataRendering()

function dataRendering(){
    pc_dataRendering();
    dp_dataRendering();
    dl_dataRendering();
};
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    if(pc_pageControl === "incomeAmount"){
        //    请求事件
        var simulationData = [
            {time:"2021-3-2",value:54245},
            {time:"2021-3-3",value:54214},
            {time:"2021-3-4",value:75841},
            {time:"2021-3-5",value:64521},
            {time:"2021-3-6",value:44145},
            {time:"2021-3-7",value:75444},
            {time:"2021-3-8",value:65412},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.toolbox.feature.dataView =  {
            show: true,
            readOnly: true,
            optionToContent: function (opt) {
                let axisData = opt.xAxis[0].data; //坐标数据
                let series = opt.series; //折线图数据
                let tdHeads = '<td  style="padding:8px 25px">时间</td>'; //表头
                let tdBodys = ''; //数据
                series.forEach((item) =>  {
                    //组装表头
                    tdHeads += `<td style="padding: 8px 25px">${item.name}</td>`;
                });
                let table = `<table border="1" style="margin-left:20px;border-collapse:collapse;font-size:14px;text-align:center"><tbody><tr>${tdHeads} </tr>`;
                for (let i = 0, l = axisData.length; i < l; i++) {
                    for (let j = 0; j < series.length; j++) {
                        //组装表数据
                        tdBodys += `<td>${series[j].data[i]}</td>`;
                    }
                    table += `<tr><td style="padding: 8px 25px">${axisData[i]}</td>${tdBodys}</tr>`;
                    tdBodys = '';
                }
                table += '</tbody></table>';
                return table;
            }
        };
        o.title.text = "收入金额";
        o.xAxis.data=time;
        o.legend.data = ["收入金额"];
        o.series = [{
            name: '收入金额',
            type: 'line',
            data:data
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "rechargeTimes") {
        var simulationData = [
            {time:"2021-3-2",value:785},
            {time:"2021-3-3",value:456},
            {time:"2021-3-4",value:845},
            {time:"2021-3-5",value:254},
            {time:"2021-3-6",value:655},
            {time:"2021-3-7",value:785},
            {time:"2021-3-8",value:885},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.toolbox.feature.dataView =  {
            show: true,
            readOnly: true,
            optionToContent: function (opt) {
                let axisData = opt.xAxis[0].data; //坐标数据
                let series = opt.series; //折线图数据
                let tdHeads = '<td  style="padding:8px 25px">时间</td>'; //表头
                let tdBodys = ''; //数据
                series.forEach((item) =>  {
                    //组装表头
                    tdHeads += `<td style="padding: 8px 25px">${item.name}</td>`;
                });
                let table = `<table border="1" style="margin-left:20px;border-collapse:collapse;font-size:14px;text-align:center"><tbody><tr>${tdHeads} </tr>`;
                for (let i = 0, l = axisData.length; i < l; i++) {
                    for (let j = 0; j < series.length; j++) {
                        //组装表数据
                        tdBodys += `<td>${series[j].data[i]}</td>`;
                    }
                    table += `<tr><td style="padding: 8px 25px">${axisData[i]}</td>${tdBodys}</tr>`;
                    tdBodys = '';
                }
                table += '</tbody></table>';
                return table;
            }
        };
        o.title.text = "充值次数";
        o.xAxis.data=time;
        o.legend.data = ["玩家充值次数"];
        o.series = [{
            name: '玩家充值次数',
            type: 'line',
            data:data
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else {
        var simulationData = [
            {time:"2021-3-2",value:115},
            {time:"2021-3-3",value:452},
            {time:"2021-3-4",value:444},
            {time:"2021-3-5",value:254},
            {time:"2021-3-6",value:856},
            {time:"2021-3-7",value:452},
            {time:"2021-3-8",value:754},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.toolbox.feature.dataView =  {
            show: true,
            readOnly: true,
            optionToContent: function (opt) {
                let axisData = opt.xAxis[0].data; //坐标数据
                let series = opt.series; //折线图数据
                let tdHeads = '<td  style="padding:8px 25px">时间</td>'; //表头
                let tdBodys = ''; //数据
                series.forEach((item) =>  {
                    //组装表头
                    tdHeads += `<td style="padding: 8px 25px">${item.name}</td>`;
                });
                let table = `<table border="1" style="margin-left:20px;border-collapse:collapse;font-size:14px;text-align:center"><tbody><tr>${tdHeads} </tr>`;
                for (let i = 0, l = axisData.length; i < l; i++) {
                    for (let j = 0; j < series.length; j++) {
                        //组装表数据
                        tdBodys += `<td>${series[j].data[i]}</td>`;
                    }
                    table += `<tr><td style="padding: 8px 25px">${axisData[i]}</td>${tdBodys}</tr>`;
                    tdBodys = '';
                }
                table += '</tbody></table>';
                return table;
            }
        };
        o.title.text = "充值人数";
        o.xAxis.data=time;
        o.legend.data = ["充值人数"];
        o.series = [{
            name: '充值人数',
            type: 'line',
            data:data
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
    var simulationData = [
        {time:"美国",value:45352},
        {time:"印度",value:24545},
        {time:"日本",value:25424},
        {time:"韩国",value:44248},
        {time:"欧洲",value:45445},
        {time:"俄罗斯",value:45578},
        {time:"中国",value:64545},
    ]
    var time = [];
    var value = [];
    var num = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].time);
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
    o.toolbox.feature.dataView =  {show: false};
    o.title.text = "各地区收入";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["收入金额"];
    o.series = [{
        name: '收入金额',
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
    var simulationData = [
        {time:"安卓市场",value:542452},
        {time:"app store",value:424551},
    ]
    var time = [];
    var value = [];
    var num = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].time);
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
    o.toolbox.feature.dataView =  {show: false};
    o.title.text = "各渠道收入";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["渠道收入"];
    o.series = [{
        name: '渠道收入',
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