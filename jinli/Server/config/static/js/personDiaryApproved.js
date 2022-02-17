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
function tableDataList (list) {
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , title: '动态通过列表'
      , height: 500
      , loading: false
      , cols: [[
        { field: 'userId', title: '发布人ID', align: "center" },
        { field: 'displayName', title: '昵称', align: "center" },
        { field: 'type', title: '发布类型', align: "center" },
        { field: 'uploadTime', title: '添加时间', align: "center" },
         { field: 'state', title: '状态', align: "center" },
        { field: 'backOfficeName', title: '操作人', align: "center" },
        { field: 'operationDate', title: '更新时间', align: "center" },
        { title: '操作', toolbar: '#barDemo', align: "center" }
      ]],
      id: 'testReload',
      page: false
    });
    table.on('tool(demo)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        // for(var i = 0 ; i<window.pathUrl.length;i++){
        //     if(window.pathUrl[i].name === "personDiaryDetails"){
        //         window.location.href = window.pathUrl[i].url+"?id="+data.id;
        //     }
        // }
        jumpDetailsUrl("personDiaryDetails", "id", data.id)
      } else if (obj.event === 'a') {
        /**
         * 没有推荐的点击事件
         * */
        $.get({
          url: window.ioApi.personDiary.recommendDiaryDetail,
          data: {
            id: data.id
          },
          success: function (res) {
            $("#recommendNum").get(0).innerHTML = '<option value="null">请选择</option>'
            for (var i = 1; i < 11; i++) {
              console.log($("#recommendNum").get(0));
              var isDisabled = false
              for (var x = 0; x < 10; x++) {
                console.log(res.unavailablePositions[x], i);
                if (i === res.unavailablePositions[x]) {
                  console.log(res.unavailablePositions[x], i);
                  isDisabled = true
                  // $("#recommendNum").get(0).innerHTML+="<option value="+i+1+" disabled>"+i+1+"位</option>"
                }
              }
              $("#recommendNum").get(0).innerHTML += isDisabled ? "<option value=" + i + " disabled>" + i + "位</option>" : "<option value=" + i + ">" + i + "位</option>"
            }
            renderForm()
            openModality("推荐设置（10/" + res.recommendSize + "）", "确认推荐", function () {
              var d = {}
              d.id = data.id
              d.recommendTime = $('input[name="reason"]:checked').val()
              d.position = $("#recommendNum").val()
              $.post({
                url: window.ioApi.personDiary.recommend,
                data: d,
                success: function (res) {
                  console.log(res);
                  if (res.code === 200) {
                    layer.open({
                      title: '提示'
                      , content: '推荐成功',
                      btn: ["确定"],
                      btn1: function (index, layero) {
                        layer.closeAll();
                        location.reload()
                      }
                    });
                  } else {
                    layer.open({
                      title: '提示'
                      , content: '推荐失败',
                      btn: ["确定"],
                      btn1: function (index, layero) {
                        layer.closeAll();
                        location.reload()
                      }
                    });
                  }
                }
              })
            })

          }
        })


      } else if (obj.event === "b") {
        /**
        *正在推荐的按钮点击事件
        * */
        $.get({
          url: window.ioApi.personDiary.recommendDiaryDetail,
          data: {
            id: data.id
          },
          success: function (res) {
            console.log(res);
            $("#recommendNum").get(0).innerHTML = '<option value="null">请选择</option>'
            for (var i = 0; i < 10; i++) {
              console.log($("#recommendNum").get(0));
              var isDisabled = false
              for (var x = 0; x < 10; x++) {
                if (res.unavailablePositions[x] !== undefined) {
                  if (i === res.unavailablePositions[x]) {
                    isDisabled = true
                    // $("#recommendNum").get(0).innerHTML+="<option value="+i+1+" disabled>"+i+1+"位</option>"
                  }
                }
              }
              $("#recommendNum").get(0).innerHTML += isDisabled ? "<option value=" + (i + 1) + " disabled>" + (i + 1) + "位</option>" : "<option value=" + (i + 1) + ">" + (i + 1) + "位</option>"
            }
            renderForm()
            openModality("推荐设置（10/" + res.recommendSize + "）", "取消推荐", function () {
              var d = {}
              d.id = data.id
              $.post({
                url: window.ioApi.personDiary.cancelRecommend,
                data: d,
                success: function (res) {
                  console.log(res);
                  if (res.code === 200) {
                    layer.open({
                      title: '提示'
                      , content: '取消成功',
                      btn: ["确定"],
                      btn1: function (index, layero) {
                        layer.closeAll();
                        location.reload()
                      }
                    });
                  } else {
                    layer.open({
                      title: '提示'
                      , content: '取消失败',
                      btn: ["确定"],
                      btn1: function (index, layero) {
                        layer.closeAll();
                        location.reload()
                      }
                    });
                  }
                }
              })
            })

          }
        })
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
    url: window.ioApi.personDiary.diaryList,
    data: {
        userId: id,
        startTime: startTime,
        endTime: endTime,
        recommendValue: parseInt($("#violationType").val()),
        diaryStatue:parseInt($("#status").val()),
      page: page,
      size: size
    },
    success: function (res) {
        console.log(res.data);
        tableDataList(timeSort(filter(res.data.content)));
        count = res.data.total;
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
    data[i].operationDate = formatDate(data[i].operationDate);
    if (data[i].type === "IMAGE") {
      data[i].type = "图片"
    } else {
      data[i].type = "小视频"
    }
    if(data[i].state === "DIARY_UNAPPROVED"){
        data[i].state = "待审核"
    }else if(data[i].state === "DIARY_APPROVAL_PASS"){
        data[i].state = "已发布"
    }else {
        data[i].state = "已拒绝"
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
    url: window.ioApi.personDiary.diaryList,
    data: {
        userId: id,
        startTime: startTime,
        endTime: endTime,
        recommendValue: parseInt($("#violationType").val()),
        diaryStatue:parseInt($("#status").val()),
      page: page,
      size: size
    },
    success: function (res) {
      tableDataList(timeSort(filter(res.data.content)));
      count = res.data.total;
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

})

function timeSort (data) {
  data.sort(function (a, b) {
    return a.operationDate < b.operationDate ? 1 : -1
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
            url: window.ioApi.personDiary.diaryList,
            data: {
              userId: id,
              startTime: startTime,
              endTime: endTime,
              recommendValue: parseInt($("#violationType").val()),
              diaryStatue:parseInt($("#status").val()),
              page: obj.curr,
              size: size
            },
            success: function (res) {
                count = res.data.total;
              page = obj.curr;
              tableDataList(timeSort(filter(res.data.content)));
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

function openModality (title, btnText, btnClick) {
  layer.open({
    type: 1,
    area: ['600px', '600px'],
    title: title
    , content: $("#gag"),
    shade: 0,
    btn: [btnText, '取消']
    , btn1: function (index, layero) {
      btnClick()
    },
    cancel: function (layero, index) {
      layer.closeAll();
    }

  });
}

function renderForm () {
  layui.use('form', function () {
    var form = layui.form;
    form.render();
  });
}