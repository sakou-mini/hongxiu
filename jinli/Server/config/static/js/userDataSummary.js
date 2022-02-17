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
  });
  laydate.render({
    elem: '#test2'
  });

});

getSummaryData()

function getSummaryData () {
  $.get({
    url: window.ioApi.user.queryPlatformUserOnlineInfo,
    data: {
      platform: parseInt($("#searchPlatform").val())
    },
    success: function (res) {
      console.log(res);
      $("#USER_NUM").text(res.statisticItems.USER_NUM)
      $("#ONLINEUSER_NUM").text(res.statisticItems.ONLINEUSER_NUM)
    }
  })
}





var exportData
var searchData = getFormData();
var nowPage = 1;
var size = 10
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
            url: window.ioApi.user.queryPlatformDayUserTotalData,
            data: searchData,
            success: function (res) {
              dataCount = res.pageInfo.total;
              tableData(res.pageInfo.content);
              initData(res.totalData)
            },
            error: function () {
              layer.open({
                title: '错误信息'
                , content: '获取列表错误'
              });
            }
          });
        }
      }
    });
  });
}

function initData (res) {
  $("#liveVisitorNum").text(res.liveVisitorNum)
  $("#giftCount").text(res.giftCount)
  $("#giftFlow").text(res.giftFlow)
  $("#liveWatchTime").text(formatSeconds(res.liveWatchTime))
  $("#banUserNum").text(res.banUserNum)
  $("#connectedLiveCount").text(res.connectedLiveCount)
  $("#bulletMessageCount").text(res.bulletMessageCount)
  $("#liveVisitorCount").text(res.liveVisitorCount)
  $("#exchangeCoinAmount").text(res.exchangeCoinAmount)
}


firstInitTable();
function firstInitTable () {
  console.log("开始");
  searchData.page = 1
  searchData.size = size
  $.get({
    url: window.ioApi.user.queryPlatformDayUserTotalData,
    data: searchData,
    success: function (res) {
      console.log(res);
      dataCount = res.pageInfo.total;
      tableData(res.pageInfo.content);
      pageGat(res.pageInfo.total, nowPage);
      initData(res.totalData)

    },
    error: function () {
      layer.open({
        title: '错误信息'
        , content: '获取列表错误'
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


$("#search").on("click",function () {
    searchData = getFormData();
    searchData.page = 1
    searchData.size = size

    $.get({
        url: window.ioApi.user.queryPlatformDayUserTotalData,
        data: searchData,
        success: function (res) {
            dataCount = res.pageInfo.total;
            tableData(res.pageInfo.content);
            pageGat(res.pageInfo.total, nowPage)
            initData(res.totalData)
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取列表错误'
            });
        }
    })
})


$("#searchBtn").on("click", function () {
    getSummaryData()
});
$("#resetBtn").on("click", function () {
  location.reload();
});

var tData;
function tableData (list) {
  for (var i = 0; i < list.length; i++) {
    list[i].recordTime = dateFormat("YYYY-mm-dd", list[i].recordTime)
    list[i].avgLiveWatchTime = formatSeconds(list[i].avgLiveWatchTime)

    list[i].liveWatchTime = formatSeconds(list[i].liveWatchTime)
  }
  exportData = list
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , limit: size
      , title: '用户列表'
      , loading: false
      , done: function (res, curr, count) {
        tData = [...res.data];
      }
      , cols: [[
        { field: 'recordTime', title: '日期', align: "center" },
        { field: 'liveVisitorCount', title: '访问直播间次数', align: "center" },
        { field: 'liveVisitorNum', title: 'ID访问直播间次数', align: "center" },
        { field: 'exchangeCoinAmount', title: '兑换货币', align: "center" },
        { field: 'avgLiveWatchTime', title: '平均停留时间', align: "center" },
        { field: 'liveWatchTime', title: '直播间停留时间', align: "center" },
        { field: 'giftCount', title: '打赏次数', align: "center" },
        { field: 'giftFlow', title: '打赏金额', align: "center" },
        { field: 'connectedLiveCount', title: '连麦次数', align: "center" },
        { field: 'bulletMessageCount', title: '弹幕条数', align: "center" }
      ]],
      id: 'testReload'
    });

  });
}



function getFormData () {
  var startTime = $("#test1").val() === "" ? "" : new Date($("#test1").val() + " 00:00:00").getTime()
  var endTime = $("#test2").val() === "" ? "" : new Date($("#test2").val() + " 00:00:00").getTime()
  var platform = parseInt($("#platform").val())
  return {
    platform,
    startTime,
    endTime
  }
}

$("#excel").on("click", function () {
  var aoa = [
    ['日期', '访问直播间次数', "ID访问直播间次数",
      "兑换货币", "平均停留时间", "直播间停留时间",
      "打赏次数", "打赏金额", "连麦次数", "弹幕条数"
    ],
  ];
  for (var i = 0; i < exportData.length; i++) {
    var newDataArr = [];
    newDataArr.push(exportData[i].recordTime);
    newDataArr.push(exportData[i].liveVisitorCount);
    newDataArr.push(exportData[i].liveVisitorNum);
    newDataArr.push(exportData[i].exchangeCoinAmount);
    newDataArr.push(exportData[i].avgLiveWatchTime);
    newDataArr.push(exportData[i].liveWatchTime);
    newDataArr.push(exportData[i].giftCount);
    newDataArr.push(exportData[i].giftFlow);
    newDataArr.push(exportData[i].connectedLiveCount);
    newDataArr.push(exportData[i].bulletMessageCount);
    aoa.push(newDataArr)
  }
  var sheet = XLSX.utils.aoa_to_sheet(aoa);
  openDownloadDialog(sheet2blob(sheet), '开播限制录入记录 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
})