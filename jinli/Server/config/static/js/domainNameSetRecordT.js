var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var slotNum = 1;
var type = "0";

function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            jump: function(obj, first){
                page = obj.curr;
                $.post({
                    url: window.ioApi.backOffice.domainRecordInfo,
                    data:{
                        page,size,platform:2
                    },
                    success:function (res) {
                        console.log(res);
                        count = res.total
                        tableData(domainRecordFilter(res.content));
                    }
                })
            }
        });
    });
}
layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});

$("#resetBtn").on("click",function () {
    location.reload();
});

function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'backOfficeName', title: '操作员',align:"center"},
                {field: 'domainLine', title: '域名配置',align:"center"},
                {field: 'oldDomain', title: '原域名',align:"center"},
                {field: 'newDomain', title: '现域名',align:"center"},
                {field: 'time', title: '修改时间',align:"center"}
            ]],
            id: 'testReload'
        });
    });
}
analogData()
function analogData() {
    $.post({
        url: window.ioApi.backOffice.domainRecordInfo,
        data:{
            page,size,platform:2
        },
        success:function (res) {
            console.log(res);
            count = res.total
            getPage()
            tableData(domainRecordFilter(res.content));
        }
    })
}

function domainRecordFilter(data) {
    for(var i=0 ;i<data.length;i++){
        data[i].domainLine = domainLineFilter(data[i].domainLine)
        data[i].time = dateFormat("YYYY-mm-dd HH:MM:SS", data[i].time)
    }
    return data
}

function domainLineFilter(domainLine) {
    switch (domainLine) {
        case "INLAND":
            return "境内线路";
        case "SOUTHEAST_ASIA":
            return "东南亚线路";
        case "OVERSEAS":
            return "境外高防线路"
    }
}