var page = 1;
var size = 10;
var count = 0;
var id = null;
var startTime = null;
var endTime = null;
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
/**
 * @method tableDataList 渲染表格
 * @param {Array} list 渲染表格需要的数据
 * */
function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '动态审核列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'userId', title: '发布人ID', align: "center" },
        { field: 'displayName', title: '昵称', align: "center" },
        { field: 'type', title: '发布类型', align: "center" },
        { field: 'uploadTime', title: '发布时间', align: "center" },
        { title: '操作', toolbar: '#barDemo', align: "center" }
      ]],
      id: 'testReload',
      page: false
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        jumpDetailsUrl("personDiaryDetails", "id", data.id)
      }
    });
  })
}
$("#resetBtn").on("click", function () {
  $("#class").val('');
  $("#test1").val('');
  $("#test2").val('');
  id = null;
  startTime = null;
  endTime = null;
  getPersonList();
});
getPersonList();
function getPersonList () {
  // startTime = isIE()? new Date(( $("#test1").val()).replace(/-/g, '/')).getTime():new Date(($("#test1").val())).getTime()
  // endTime = isIE()? new Date(( $("#test2").val()).replace(/-/g, '/')).getTime():new Date(($("#test2").val())).getTime();
  $.get({
    url: window.ioApi.personDiary.getPersonDiaryByUserIdAndTime,
    data: {
      id: id,
      startTime: startTime,
      endTime: endTime,
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(timeSort(filter(res.data)));
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
function filter (data) {
  for (var i = 0; i < data.length; i++) {
    data[i].uploadTime = formatDate(data[i].uploadTime);
    if (data[i].type === "IMAGE") {
      data[i].type = "图片"
    } else {
      data[i].type = "小视频"
    }
  }
  return data;
}


$("#searchBtn").on("click", function () {
  id = $("#class").val();
  page = 1;
  if ($("#test1").val() !== "" && $("#test2").val() !== "") {
    startTime = isIE() ? new Date(($("#test1").val()).replace(/-/g, '/')).getTime() : new Date(($("#test1").val())).getTime()
    endTime = isIE() ? new Date(($("#test2").val()).replace(/-/g, '/')).getTime() : new Date(($("#test2").val())).getTime();
  }

  $.get({
    url: window.ioApi.personDiary.getPersonDiaryByUserIdAndTime,
    data: {
      id: id,
      startTime: startTime,
      endTime: endTime,
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(timeSort(filter(res.data)));
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

});

/**
 * @method timeSort 根据时间排序
 * @param {Array} data 需要根据时间排序的数组
 * @return {Array} 返回时间从近到远的数组
 * */
function timeSort (data) {
  data.sort(function (a, b) {
    return a.uploadTime < b.uploadTime ? 1 : -1
  });
  return data;
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
        if (!first) {
          page = obj.curr;
          console.log(obj.curr)
          id = $("#class").val();
          if ($("#test1").val() !== "" && $("#test2").val() !== "") {
            startTime = isIE() ? new Date(($("#test1").val()).replace(/-/g, '/')).getTime() : new Date(($("#test1").val())).getTime()
            endTime = isIE() ? new Date(($("#test2").val()).replace(/-/g, '/')).getTime() : new Date(($("#test2").val())).getTime();
          }
          $.get({
            url: window.ioApi.personDiary.getPersonDiaryByUserIdAndTime,
            data: {
              id: id,
              startTime: startTime,
              endTime: endTime,
              page: obj.curr,
              size: size
            },
            success: function (res) {
              count = res.count;
              page = obj.curr;
              tableDataList(timeSort(filter(res.data)));
              pageGat(res.count, page)
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
    });
  });
}


var time1;
var time2;


timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click", function () {
  theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click", function () {
  if (verificationTheTTime() === "ok") {
    if (isIE()) {
      time1 = new Date(($("#theT_openTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
      time2 = new Date(($("#theT_endTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
    } else {
      time1 = new Date(($("#theT_openTime").val() + " 00:00:00")).getTime();
      time2 = new Date(($("#theT_endTime").val() + " 00:00:00")).getTime();
    }
    theTclose();
    innerTime();

  }
})

$("#theT_t").on("click", function () {
  $("#theT_openTime").val(fun_date(0));
  $("#theT_endTime").val(fun_date(0));
  if (isIE()) {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
  } else {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00")).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00")).getTime();
  }
  theTclose();
  innerTime();

})
$("#theT_y").on("click", function () {
  $("#theT_openTime").val(fun_date(-1));
  $("#theT_endTime").val(fun_date(0));
  if (isIE()) {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
  } else {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00")).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00")).getTime();
  }
  theTclose();
  innerTime();

})
$("#theT_w").on("click", function () {
  $("#theT_openTime").val(fun_date(-6));
  $("#theT_endTime").val(fun_date(0));
  if (isIE()) {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
  } else {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00")).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00")).getTime();
  }
  theTclose();
  innerTime();

})
$("#theT_m").on("click", function () {
  $("#theT_openTime").val(fun_date(-30));
  $("#theT_endTime").val(fun_date(0));
  if (isIE()) {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00").replace(/-/g, '/')).getTime();
  } else {
    time1 = new Date(($("#theT_openTime").val() + " 00:00:00")).getTime();
    time2 = new Date(($("#theT_endTime").val() + " 00:00:00")).getTime();
  }
  theTclose();
  innerTime();

})

// 刷新时间选择器显示时间
function innerTime () {
  var day1 = formatSpecificDate(new Date($("#theT_openTime").val()));
  var day2 = formatSpecificDate(new Date($("#theT_endTime").val()));
  $("#theT_open").val(day1 + " ~ " + day2);
}

//初始化时间选择器时间
$("#theT_open").val(fun_date(-6) + " ~ " + fun_date(0));

//时间控制器取消按钮
$("#theT_cancel").on("click", function () {
  theTclose();
});

//时间选择器显示方法
function theTshow () {
  $("#theT_boxHidden").attr("class", "layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose () {
  $("#theT_boxHidden").attr("class", "layui-row theT_boxHidden theT_hidden")
}


// 时间选择器初始化
function timeUpdate () {
  layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
      elem: '#theT_openTime',
      value: fun_date(-6),
      max: 0
    });
    laydate.render({
      elem: '#theT_endTime',
      value: fun_date(0),
      max: 0
    });
  });
}