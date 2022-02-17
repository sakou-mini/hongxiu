var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var slotNum = 1;
var type = "0";
var platform = 1
layui.use('form', function(){
    var form = layui.form;
});

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
                page = obj.curr;
                if(!first){
                    analogData();
                }
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

analogData();
function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'id', title: '礼物ID'},
                {field: 'type', title: '类型'},
                {field: 'giftName', title: '礼物名称'},
                {field: 'coin', title: '礼物金额'},
                {title: '操作', toolbar: '#barDemo'}
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'a'){
                $("#name").val(data.giftName);
                $("#coin").val(data.p);
                layer.open({
                    type:1,
                    area:['500px',"300px"],
                    title: '修改礼物信息'
                    ,content: $("#test"),
                    shade: 0,
                    btn: ['提交']
                    ,btn1: function(index, layero){
                        $.post({
                            url:window.ioApi.gift.changePrice,
                            data:{
                                giftId:parseInt(data.id),
                                price:parseInt( $("#coin").val()),
                                platform:platform
                            },
                            success:function (res) {
                                console.log(res);
                                if(res.code === 200){
                                    layer.open({
                                        title: '提示'
                                        ,content: "提交成功",
                                        area:['200px'],
                                        btn: ['确认']
                                        ,btn1: function(index, layero){
                                            location.reload()
                                        },
                                        cancel: function(layero,index){
                                            location.reload()
                                        }
                                    });
                                }
                            }
                        })
                    },
                    cancel: function(layero,index){
                        layer.closeAll();
                    }
                });
            }else if(obj.event === 'b'){
                $.post({
                    url:window.ioApi.gift.openOrCloseGift,
                    data:{
                        giftId:parseInt(data.id),
                        enable: false,
                        platform:platform
                    },
                    success:function (res) {
                        location.reload()
                    }
                })
            }else if(obj.event === 'c'){
                $.post({
                    url:window.ioApi.gift.openOrCloseGift,
                    data:{
                        giftId:parseInt(data.id),
                        enable: true,
                        platform:platform
                    },
                    success:function (res) {
                        location.reload()
                    }
                })
            }
        });
        roleTableBtn(["CHANGEPRICE"],"a")
        roleTableBtn(["OPENORCLOSEGIFT"],"b")
        roleTableBtn(["OPENORCLOSEGIFT"],"c")

    });
}
$("#searchBtn").on("click",function () {
    platform = parseInt($("#platform").val())
    type = $("#violationType").val() !== "0"
    analogData()
})

function analogData() {
    $.post({
        url: window.ioApi.gift.giftPageInfo,
        data:{
            page:page,
            size:size,
            luxury:type,
            platform:platform
        },
        success:function (res) {
            console.log(res);
            count = res.total;
            var data = [];
            var gd = res.content;
            for(var i = 0;i<gd.length;i++){

                if(gd[i].giftId !== "10000" || platform === 1) {
                    var d = {};
                    d.id = gd[i].giftId;
                    var gift = getGiftMsg(d.id + "");
                    d.type = gift.type;
                    d.giftName = gift.name;
                    if (d.id === "10000") {
                        d.coin = gd[i].price + "强吻";
                    } else {
                        d.coin = gd[i].price + "金币";
                        d.p = gd[i].price
                    }
                    d.state = gd[i].enable;
                    data.push(d)
                }
            }
            getPage()
            tableData(data);
        }
    })


}