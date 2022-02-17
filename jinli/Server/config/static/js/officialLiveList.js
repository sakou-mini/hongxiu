$("#addBtn").on("click", function () {
  jumpUrl("newOfficialLive")
});

layui.use('layer', function () {
  var layer = layui.layer
});
layui.use('element', function () {
  var element = layui.element;
});

displayRoleElement(["OFFICIALLIVEADD"], "addBtn")

var page = 1;
var size = 10;
var count = 0;

function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo',
      data: list,
      title: '官方直播间列表',
      height: 500,
      loading: false,
      cols: [[
        { field: 'roomName', title: '直播间信息', templet: '#imgTemplet' },
        { field: 'gameType', title: '游戏类型', align: "center" },
        { field: 'giftTurnover', title: '礼物流水', align: "center" },
        { field: 'gameTurnover', title: '游戏流水', align: "center" },
        { field: 'roomPeople', title: '直播间人次', align: "center" },
        { field: 'nowRoomPeople', title: '当前直播间人数', align: "center" },
        { field: 'roomCreateDate', title: "直播间开启时间", align: "center" },
        { title: '操作', toolbar: '#barDemo', align: "center" }
      ]],
      id: 'testReload',
      page: false
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        layer.confirm('确认关闭直播间吗？', {
          btn: ['确认', '取消'],
          area: ['300px']
        }, function (index) {
          $.get({
            url: window.ioApi.officialLive.closeOfficialLive,
            data: { liveUserId: data.liveUserId },
            success: function (res) {
              location.reload();
            }
          })
        });
      }
    });
    roleTableBtn(["OFFICIALLIVECOLSE"], "detail")
  })
}

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
            url: window.ioApi.officialLive.getOfficialLiveList,
            data: {
              page: page,
              size: size
            },
            success: function (res) {
              tableDataList(dataFilter(res.data));
              count = res.count;
              page = obj.curr;
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
getData();
function getData () {
  $.get({
    url: window.ioApi.officialLive.getOfficialLiveList,
    data: {
      page: page,
      size: size
    },
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
    data[i].roomCreateDate = formatDate(data[i].roomCreateDate);
    data[i].gameType = gameTypeFormat(data[i].gameType)
  }
  return data
}


