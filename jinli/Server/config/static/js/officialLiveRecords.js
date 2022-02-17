$("#addBtn").on("click", function () {
  jumpUrl("newOfficialLive")
})

layui.use('form', function () {
  var form = layui.form;
  form.on('select(type)', function (data) {
    if (data.value === "0") {
      type = false;
    } else {
      type = true
    }
    page = 1;
    analogData()
  });
});
layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1',
  });
  laydate.render({
    elem: '#test2',
  });
});

layui.use('layer', function () {
  var layer = layui.layer
});
layui.use('element', function () {
  var element = layui.element;
});

var page = 1;
var size = 10;
var count = 0;

function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo',
      data: list,
      title: '官方直播间列表列表',
      height: 500,
      loading: false,
      cols: [[
        { field: 'roomName', title: '直播间名字', align: "center" },
        { field: 'roomDisplayId', title: '直播间ID', align: "center" },
        { field: 'gameType', title: '游戏类型', align: "center" },
        { field: 'giftTurnover', title: '礼物流水', align: "center" },
        { field: 'gameTurnover', title: '游戏流水', align: "center" },
        { field: 'roomPeople', title: '直播间人次', align: "center" },
        { field: 'roomCloseDate', title: '直播间关闭时间', align: "center" },
        { field: 'backOfficeName', title: "操作人", align: "center" },
      ]],
      id: 'testReload',
      page: false
    });
  })
}

$("#resetBtn").on("click", function () {
  location.reload();
});

function getPage () {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: page,
      jump: function (obj, first) {
        page = obj.curr;
        if (!first) {
          $.get({
            url: window.ioApi.officialLive.getCloseOfficialList,
            data: getSearchData(),
            success: function (res) {
              console.log(res);
              tableDataList(dataFilter(res.data));
              count = res.count;
              getPage()
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '获取竞猜列表失败'
              });
            }
          })
        }
      }
    });
  })
}


$("#searchBtn").on("click", function () {
  $.get({
    url: window.ioApi.officialLive.getCloseOfficialList,
    data: getSearchData(1),
    success: function (res) {
      console.log(res);
      tableDataList(dataFilter(res.data));
      count = res.count;
      getPage()
    }
  })
})


getData();
function getData () {
  $.get({
    url: window.ioApi.officialLive.getCloseOfficialList,
    data: getSearchData(),
    success: function (res) {
      console.log(res);
      tableDataList(dataFilter(res.data));
      count = res.count;
      getPage()
    }
  })
}

function dataFilter (data) {
  for (var i = 0; i < data.length; i++) {
    data[i].roomCloseDate = formatDate(data[i].roomCloseDate);
    data[i].gameType = gameTypeFormat(data[i].gameType);
    if (data[i].backOfficeName === "SYSTEM") {
      data[i].backOfficeName = "系统"
    }
  }
  return data
}

function getSearchData (p) {
  var data = {
    page: p || page,
    size,
    gameType: parseInt($("#violationType").val())
  };
  $("#test1").val() === "" ? data.startTime = "" : data.startTime = isIE() ?
    new Date(($("#test1").val() + " 00:00:00").replace(/-/g, '/')).getTime() : new Date(($("#test1").val() + " 00:00:00")).getTime()
  $("#test2").val() === "" ? data.endTime = "" : data.endTime = isIE() ?
    new Date(($("#test2").val() + " 00:00:00").replace(/-/g, '/')).getTime() : new Date(($("#test2").val() + " 00:00:00")).getTime()
  return data
}