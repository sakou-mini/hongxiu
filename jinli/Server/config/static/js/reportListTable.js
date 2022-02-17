var page = 1;
var size = 10;
var count = 0;
var violationType = null;
var informantId = null;
var reportedId = null;
var reportedRoomId = null;
var dateStart = null;
var dateEnd = null;
var handle = 0;
var test;
var isHandle = false;
function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '举报列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'informantNickName', title: '举报人', width: 210, align: "center" },
        // {field: 'informantId', title: '举报人ID',width: 210},
        { field: 'reportedRoomId', title: '被举报房间号', width: 210, align: "center" },
        { field: 'reportedNickName', title: '主播', width: 210, align: "center" },
        // {field: 'reportedId', title: '主播ID',width:210},
        { field: 'violationType', title: '类型', width: 100, align: "center" },
        { field: 'createDate', title: '举报时间', align: "center" },
        { field: 'state', title: '状态', align: "center" },
        { title: '操作', toolbar: '#barDemo', align: "center" }
      ]],
      id: 'testReload',
      page: false
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        // window.location.href = "/report/reportDetails?id="+data.id;
        jumpDetailsUrl("reportDetails", "id", data.id)
      }
      else if (obj.event === 'edit') {
        // window.location.href = "/report/reportDetails?id="+data.id;
        jumpDetailsUrl("reportDetails", "id", data.id)
      }
    });
  })
}
layui.use('form', function () {
  var form = layui.form;
  form.on('submit(formDemo)', function (data) {
    layer.msg(JSON.stringify(data.field));
    return false;
  });
  form.on('select(handle)', function (data) {
    handle = data.value;
    console.log(handle);
    if (handle === "0") {
      isHandle = false
    }
    else {
      isHandle = true
    }
  });
});
searchReportList();
$("#resetBtn").on("click", function () {
  window.history.go(0)
});

function TypeFilter (dataList) {
  var newDataList = [];
  for (var i = 0; i < dataList.length; i++) {
    dataList[i].informantNickName = dataList[i].informantNickName + "(" + dataList[i].informantId + ")";
    dataList[i].reportedNickName = dataList[i].reportedNickName + "(" + dataList[i].reportedId + ")";
    dataList[i].createDate = formatDate(dataList[i].createDate);
    if (dataList[i].isHandle) {
      dataList[i].state = "已处理"
    } else {
      dataList[i].state = "未处理"
    }
    if (dataList[i].violationType == "VULGAR") {
      dataList[i].violationType = "色情低俗";
      newDataList.push(dataList[i])
    }
    else if (dataList[i].violationType == "ILLEGAL") {
      dataList[i].violationType = "非法行为";
      newDataList.push(dataList[i])
    }
    else if (dataList[i].violationType == "CHEAT") {
      dataList[i].violationType = "诈骗";
      newDataList.push(dataList[i])
    }
    else if (dataList[i].violationType == "REACTIONARY") {
      dataList[i].violationType = "反动";
      newDataList.push(dataList[i])
    }
    else if (dataList[i].violationType == "OTHERS") {
      dataList[i].violationType = "其他";
      newDataList.push(dataList[i])
    }
  }
  return newDataList;
}

//搜索处理
$("#searchBtn").on("click", function () {
  searchReportList();
});
function searchReportList () {
  violationType = $("#violationType").val();
  informantId = $("#element").val().replace(/(^\s*)|(\s*$)/g, "");
  reportedId = $("#move").val().replace(/(^\s*)|(\s*$)/g, "");
  reportedRoomId = $("#class").val().replace(/(^\s*)|(\s*$)/g, "");
  var regex = /^[0-9A-Za-z]+$/;
  dateStart = $("#test1").val();
  dateEnd = $("#test2").val();
  if (informantId) {
    if (!regex.test(informantId)) {
      layer.msg('错误的举报人ID格式');
      return
    }
  }
  if (reportedId) {
    if (!regex.test(reportedId)) {
      layer.msg('错误的被举报人ID格式');
      return
    }
  }
  if (reportedRoomId) {
    if (!regex.test(reportedRoomId)) {
      layer.msg('错误的房间ID格式');
      return
    }
  }

  if (violationType) {
    if (dateStart && dateEnd) {
      dateStart = new Date($("#test1").val());
      dateEnd = new Date($("#test2").val());
      $.get({
        url: window.ioApi.report.getReportByConditionQuery,
        data: {
          violationType: parseInt(violationType),
          informantId: informantId,
          reportedId: reportedId,
          reportedRoomId: reportedRoomId,
          dateStart: dateStart,
          dateEnd: dateEnd,
          isHandle: isHandle,
          page: page,
          size: size
        },
        success: function (res) {
          tableDataList(timeSort(TypeFilter(res.data)));
          count = res.count;
          page = 1;
          pageGat()
        },
        error: function () {
          layer.open({
            title: '错误信息'
            , content: '服务器错误'
          });
        }
      })
    }
    else {
      $.get({
        url: window.ioApi.report.getReportByConditionQuery,
        data: {
          violationType: parseInt(violationType),
          informantId: informantId,
          reportedId: reportedId,
          reportedRoomId: reportedRoomId,
          isHandle: isHandle,
          page: page,
          size: size
        },
        success: function (res) {
          tableDataList(timeSort(TypeFilter(res.data)));
          count = res.count;
          page = 1;
          pageGat()
        },
        error: function () {
          layer.open({
            title: '错误信息'
            , content: '服务器错误'
          });
        }
      })
    }
  }
  else {
    if (dateStart && dateEnd) {
      dateStart = new Date($("#test1").val());
      dateEnd = new Date($("#test2").val());
      console.log("有时间请求");
      $.get({
        url: window.ioApi.report.getReportByConditionQueryNotType,
        data: {
          informantId: informantId,
          reportedId: reportedId,
          reportedRoomId: reportedRoomId,
          dateStart: dateStart,
          dateEnd: dateEnd,
          isHandle: isHandle,
          page: page,
          size: size
        },
        success: function (res) {
          tableDataList(timeSort(TypeFilter(res.data)));
          count = res.count;
          page = 1;
          pageGat()
        },
        error: function () {
          layer.open({
            title: '错误信息'
            , content: '服务器错误'
          });
        }
      })
    }
    else {
      $.get({
        url: window.ioApi.report.getReportByConditionQueryNotType,
        data: {
          informantId: informantId,
          reportedId: reportedId,
          reportedRoomId: reportedRoomId,
          isHandle: isHandle,
          page: page,
          size: size
        },
        success: function (res) {
          tableDataList(timeSort(TypeFilter(res.data)));
          count = res.count;
          page = 1;
          pageGat()
        },
        error: function () {
          layer.open({
            title: '错误信息'
            , content: '服务器错误'
          });
        }
      })
    }

  }

}

function timeSort (data) {
  data.sort(function (a, b) {
    return a.createDate < b.createDate ? 1 : -1
  });
  return data;
}

function isHandleSort (data) {
  var handleArr = [];
  var notHandleArr = [];
  for (var i = 0; i < data.length; i++) {
    if (data[i].isHandle) {
      handleArr.push(data[i])
    }
    else {
      notHandleArr.push(data[i])
    }
  }
  return notHandleArr.concat(handleArr)
}

function pageGat () {
  layui.use('laypage', function () {
    var laypage = layui.laypage;
    laypage.render({
      elem: 'paging',
      count: count,
      limit: size,
      curr: page,
      jump: function (obj, first) {
        console.log(obj);
        if (!first) {
          violationType = $("#violationType").val();
          informantId = $("#element").val().replace(/(^\s*)|(\s*$)/g, "");
          reportedId = $("#move").val().replace(/(^\s*)|(\s*$)/g, "");
          reportedRoomId = $("#class").val().replace(/(^\s*)|(\s*$)/g, "");
          dateStart = $("#test1").val();
          dateEnd = $("#test2").val();
          page = obj.curr;
          dateStart = $("#test1").val();
          dateEnd = $("#test2").val();
          id = $("#class").val();
          if (handle === 0) {
            isHandle = false
          }
          else {
            isHandle = true
          }
          if (violationType) {
            if (dateStart && dateEnd) {
              dateStart = new Date($("#test1").val());
              dateEnd = new Date($("#test2").val());
              $.get({
                url: window.ioApi.report.getReportByConditionQuery,
                data: {
                  violationType: parseInt(violationType),
                  informantId: informantId,
                  reportedId: reportedId,
                  reportedRoomId: reportedRoomId,
                  dateStart: dateStart,
                  dateEnd: dateEnd,
                  isHandle: isHandle,
                  page: page,
                  size: size
                },
                success: function (res) {
                  tableDataList(timeSort(TypeFilter(res.data)));
                  count = res.count;
                  page = obj.curr;
                },
                error: function () {
                  layer.open({
                    title: '错误信息'
                    , content: '服务器错误'
                  });
                }
              })
            }
            else {
              $.get({
                url: window.ioApi.report.getReportByConditionQuery,
                data: {
                  violationType: parseInt(violationType),
                  informantId: informantId,
                  reportedId: reportedId,
                  reportedRoomId: reportedRoomId,
                  isHandle: isHandle,
                  page: page,
                  size: size
                },
                success: function (res) {
                  tableDataList(timeSort(TypeFilter(res.data)));
                  count = res.count;
                  page = 1;
                },
                error: function () {
                  layer.open({
                    title: '错误信息'
                    , content: '服务器错误'
                  });
                }
              })
            }
          }
          else {
            if (dateStart && dateEnd) {
              dateStart = new Date($("#test1").val());
              dateEnd = new Date($("#test2").val());
              console.log("有时间请求");
              $.get({
                url: window.ioApi.report.getReportByConditionQueryNotType,
                data: {
                  informantId: informantId,
                  reportedId: reportedId,
                  reportedRoomId: reportedRoomId,
                  dateStart: dateStart,
                  dateEnd: dateEnd,
                  isHandle: isHandle,
                  page: page,
                  size: size
                },
                success: function (res) {
                  tableDataList(timeSort(TypeFilter(res.data)));
                  count = res.count;
                  page = 1;
                },
                error: function () {
                  layer.open({
                    title: '错误信息'
                    , content: '服务器错误'
                  });
                }
              })
            }
            else {
              $.get({
                url: window.ioApi.report.getReportByConditionQueryNotType,
                data: {
                  informantId: informantId,
                  reportedId: reportedId,
                  reportedRoomId: reportedRoomId,
                  isHandle: isHandle,
                  page: page,
                  size: size
                },
                success: function (res) {
                  tableDataList(timeSort(TypeFilter(res.data)));
                  count = res.count;
                  page = 1;
                },
                error: function () {
                  layer.open({
                    title: '错误信息'
                    , content: '服务器错误'
                  });
                }
              })
            }
          }
        }
      }
    });
  });
}