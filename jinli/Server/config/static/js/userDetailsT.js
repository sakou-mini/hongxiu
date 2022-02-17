layui.use('layer', function () {
  var layer = layui.layer
});
layui.use('element', function () {
  var element = layui.element;
});

layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1'
    , type: 'datetime'
    , value: fun_date(-6) + " 00:00:00"
  });
  laydate.render({
    elem: '#test2'
    , type: 'datetime'
    , value: fun_date(0) + " 23:59:59"
  });
})

var exportData

var searchData = getFormData();
var nowPage = 1;
var size = 10
var banUserControl = 0;
function pageGat (count, nowpage) {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: nowpage,
      layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
      jump: function (obj, first) {
        size = obj.limit;
        if (!first) {
          searchData = getFormData();
          searchData.page = obj.curr;
          searchData.size = obj.limit;

          $.get({
            url: window.ioApi.user.userLiveWatchRecord,
            data: searchData,
            success: function (res) {
              console.log("pageLOg");
              dataCount = res.total;
              tableData(res.content);
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '获取主播列表错误'
              });
            }
          });
        }
      }
    });
  });
}
firstInitTable();
function firstInitTable () {
  searchData.page = 1
  searchData.size = size
  $.get({
    url: window.ioApi.user.userLiveWatchRecord,
    data: searchData,
    success: function (res) {
      console.log(res);
      dataCount = res.total;
      tableData(res.content);
      pageGat(res.total, nowPage);
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取用户列表错误'
      });
    }
  })
}

layui.use('layer', function () {
  var layer = layui.layer
});
// 插件加载
layui.use('element', function () {
  var element = layui.element;
});

$("#searchBtn").on("click", function () {
  searchData = getFormData();
  searchData.page = 1
  searchData.size = size
  $.get({
    url: window.ioApi.user.userLiveWatchRecord,
    data: searchData,
    success: function (res) {
      dataCount = res.total;
      tableData(res.content);
      pageGat(res.total, nowPage)
    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取主播列表错误'
      });
    }
  })
});
$("#resetBtn").on("click", function () {
  location.reload();
});

var tData;
function tableData (list) {
  list = dataFormat(list)
  exportData = list
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , limit: size
      , title: '用户列表'
      , loading: false
      , cellMinWidth: 150
      , cols: [[

        { field: 'enterRoomDate', title: '日期', align: "center", width: 180 },
        { field: 'userName', title: '用户昵称', align: "center" },
        { field: 'userId', title: '用户ID', align: "center" },
        { field: 'roomDisplayId', title: '访问直播间ID', align: "center" },
        { field: 'liveUserName', title: '访问主播名称', align: "center" },
        { field: 'enterRoomTime', title: '访问时间', align: "center" },
        { field: 'quitRoomTime', title: '离开时间', align: "center" },
        { field: 'watchTime', title: '观看时长', align: "center" },
        { field: 'giftCount', title: '打赏次数', align: "center" },
        { field: 'giftFlow', title: '打赏金额', align: "center" },
        { field: 'connectedLiveCount', title: '连麦次数', align: "center" },
        { field: 'bulletMessageCount', title: '弹幕互动', align: "center" },
        { field: 'phoneBrand', title: '登录设备', align: "center" },
        { field: 'ip', title: '登录IP', align: "center" },
      ]],
      id: 'testReload'
    });
  });
}


function getFormData () {
  var userId = GetQueryString("userId")
  var startTime = $("#test1").val() === "" ? new Date(fun_date(-6)).getTime() : new Date($("#test1").val()).getTime()
  var endTime = $("#test2").val() === "" ? new Date(fun_date(0) + " 23:59:59").getTime() : new Date($("#test2").val()).getTime()
  var platform = 2
  return {
    userId,
    startTime,
    endTime,
    platform
  }
}


$("#excel").on("click", function () {
  var aoa = [
    ['日期', '用户昵称', "用户ID",
      "访问直播间ID", "访问主播名称", "访问时间",
      "离开时间", "观看时长", "打赏次数", "打赏金额",
      "连麦次数", "弹幕互动", "登录设备", "登录IP"
    ],
  ];
  for (var i = 0; i < exportData.length; i++) {
    var newDataArr = [];
    newDataArr.push(exportData[i].enterRoomDate);
    newDataArr.push(exportData[i].userName);
    newDataArr.push(exportData[i].userId);
    newDataArr.push(exportData[i].roomDisplayId);
    newDataArr.push(exportData[i].liveUserName);
    newDataArr.push(exportData[i].enterRoomTime);
    newDataArr.push(exportData[i].quitRoomTime);
    newDataArr.push(exportData[i].watchTime);
    newDataArr.push(exportData[i].giftCount);
    newDataArr.push(exportData[i].giftFlow);
    newDataArr.push(exportData[i].connectedLiveCount);
    newDataArr.push(exportData[i].bulletMessageCount);
    newDataArr.push(exportData[i].phoneBrand);
    newDataArr.push(exportData[i].ip);
    aoa.push(newDataArr)
  }
  var sheet = XLSX.utils.aoa_to_sheet(aoa);
  openDownloadDialog(sheet2blob(sheet), '直播观看记录 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
})


function dataFormat (data) {
  var newDataArr = []
  for (var i = 0; i < data.length; i++) {
    var d = {}
    d.enterRoomDate = dateFormat("YYYY-mm-dd", data[i].liveWatchRecord.enterRoomTime)
    d.userName = data[i].userDisplayName
    d.userId = data[i].liveWatchRecord.userId
    d.roomDisplayId = data[i].liveWatchRecord.roomDisplayId
    d.liveUserName = data[i].liveUserDisplayName
    d.enterRoomTime = dateFormat("HH:MM:SS", data[i].liveWatchRecord.enterRoomTime)
    d.quitRoomTime = dateFormat("HH:MM:SS", data[i].liveWatchRecord.quitRoomTime)
    d.watchTime = formatSeconds(data[i].liveWatchRecord.watchTime)
    d.giftCount = data[i].liveWatchRecord.giftCount
    d.giftFlow = data[i].liveWatchRecord.giftFlow
    d.connectedLiveCount = data[i].liveWatchRecord.connectedLiveCount
    d.bulletMessageCount = data[i].liveWatchRecord.bulletMessageCount
    d.phoneBrand = brandFormat(data[i].liveWatchRecord.phoneBrand, "")
    d.ip = data[i].liveWatchRecord.ip
    newDataArr.push(d)
  }
  return newDataArr
}