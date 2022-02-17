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
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                size =obj.limit;
                page = obj.curr;
                $.post({
                    url: window.ioApi.backOfficeMessage.rollMessageRecord,
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
                {field: 'rollDisPlayTimeX', title: '时间范围',width : 380,align:"center"},
                {field: 'rollIntervalTimeX', title: '间隔时间',align:"center"},
                {field: 'backOfficeAccountName', title: '操作员',align:"center"},
                {field: 'recordTimeX', title: '操作时间',align:"center"},
                {field: 'takeEffect', title: '是否生效',width: 100,align:"center"},
                {field: 'newMessage', title: '内容',align:"center"},
                {title: '操作', toolbar: '#barDemo',align:"center"}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'b'){
                jumpEncodeUrl("backOfficeMessageT",data)
            }
        });
    });
}
analogData()
function analogData() {
    $.post({
        url: window.ioApi.backOfficeMessage.rollMessageRecord,
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
        data[i].rollDisPlayTimeX = dateFormat("YYYY-mm-dd HH:MM:SS", data[i].rollDisPlayStartTime) + "至" + dateFormat("YYYY-mm-dd HH:MM:SS", data[i].rollDisPlayEndTime)
        data[i].rollIntervalTimeX = formatSeconds(data[i].rollIntervalTime)
        data[i].recordTimeX = dateFormat("YYYY-mm-dd HH:MM:SS",  data[i].recordTime)
        data[i].takeEffect =   data[i].takeEffect ? "生效中" : "已过期"
    }
    return data
}

