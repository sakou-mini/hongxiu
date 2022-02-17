layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test1',
  });
});
layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#openLiveDate',
    value: fun_date(0)
  });


})
var page = 1
var size = 10
var count = 0
var exportData

layui.use('layer', function () {
  var layer = layui.layer
});
// 插件加载
layui.use('element', function () {
  var element = layui.element;
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
        var limitDate = $("#test1").val() === "" ? "" : new Date($("#test1").val() + " 00:00:00").getTime()
        var startHour = $("#startHour").val() === "noData" ? "" : parseInt($("#startHour").val())
        var platform = parseInt($("#platform").val())
        var liveUserId = $("#searchLiveUserId").val()
        $.get({
          url: window.ioApi.liveUser.requestLiveLimitAddRecord,
          data: {
            page: page,
            size: size,
            platform,
            limitDate,
            startHour,
            liveUserId

          },
          success: function (res) {
            console.log(res);
            count = res.total

            tableData(dataInfo(res.content))
          }
        })
      }
    });
  });
}



function tableData (list) {
  exportData = list
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#demo'
      , data: list
      , limit: count
      , title: ""
      , loading: false
      , cols: [[
        { field: 'limitDate', title: '日期' },
        { field: 'account', title: '开播账号' },
        { field: 'displayName', title: '主播昵称' },
        { field: 'liveUserId', title: '主播ID' },
        { field: 'liveStartHour', title: '上播时间' },
        { field: 'liveEndHour', title: '下播时间' },
        { field: 'platform', title: '主播平台' },
        { field: 'addWhiteList', title: '是否加白' },
      ]],
      id: 'testReload'
    });

  });
}






analogData();

function analogData () {
  var limitDate = $("#test1").val() === "" ? "" : new Date($("#test1").val() + " 00:00:00").getTime()
  var startHour = $("#startHour").val() === "noData" ? "" : parseInt($("#startHour").val())
  var platform = parseInt($("#platform").val())
  var liveUserId = $("#searchLiveUserId").val()
  $.get({
    url: window.ioApi.liveUser.requestLiveLimitAddRecord,
    data: {
      page: page,
      size: size,
      platform,
      limitDate,
      startHour,
      liveUserId
    },
    success: function (res) {
      console.log(res);
      count = res.total
      tableData(dataInfo(res.content))
      getPage()
    }
  })
}





$("#searchBtn").on("click", function () {
  page = 1;
  var limitDate = $("#test1").val() === "" ? "" : new Date($("#test1").val() + " 00:00:00").getTime()
  var startHour = $("#startHour").val() === "noData" ? "" : parseInt($("#startHour").val())
  var platform = parseInt($("#platform").val())
  var liveUserId = $("#searchLiveUserId").val()
  $.get({
    url: window.ioApi.liveUser.requestLiveLimitAddRecord,
    data: {
      page: page,
      size: size,
      platform,
      limitDate,
      startHour,
      liveUserId
    },
    success: function (res) {
      console.log(res);
      count = res.total
      tableData(dataInfo(res.content));

    }
  })
})




function dataInfo (data) {
  console.log(data);
  var list = []
  for (var i = 0; i < data.length; i++) {
    var d = {}
    d.addWhiteList = data[i].addWhiteList ? "是" : "否";
    d.liveEndHour = data[i].liveEndHour < 10 ? "0" + data[i].liveEndHour + ":00:00" : data[i].liveEndHour + ":00:00";
    d.liveStartHour = data[i].liveStartHour < 10 ? "0" + data[i].liveStartHour + ":00:00" : data[i].liveStartHour + ":00:00";
    d.limitDate = dateFormat("YYYY-mm-dd", data[i].limitDate)
    d.account = data[i].account
    d.displayName = data[i].displayName
    d.liveUserId = data[i].liveUserId;
    d.platform = getPlatform(data[i].platform)
    list.push(d)
  }
  return list
}



$("#upload").on("click", function () {

  layer.open({
    type: 1,
    area: ['500px', '400px'],
    title: '添加考勤记录'
    , content: $("#whiteListExcl"),
    shade: 0,
    btn: ['提交']
    , btn1: function (index, layero) {
      console.log("upload");
      var fd = new FormData(document.forms[0]);
      fd.append("file", $('input[name="file"]').get(0).files[0]);
      fd.append("platform", parseInt($("#uploadPlatform").val()));
      $.post({
        url: window.ioApi.liveUser.uploadLiveLimitExcel,
        data: fd,
        cache: false,
        xhr: function () {
          return $.ajaxSettings.xhr();
        },
        contentType: false,
        processData: false,
        success: function (res) {

          if (res.code === 510) {
            layer.open({
              title: '提示'
              , content: '上传文件中包含了不存在的主播或者其他平台主播',
              btn: ["确定"],
              btn1: function (index, layero) {
                layer.close(index);
              }
            });
          } else if (res.code === 513) {
            layer.open({
              title: '提示'
              , content: '上传了错误的模板文件',
              btn: ["确定"],
              btn1: function (index, layero) {
                layer.close(index);
              }
            });
          } else {
            layer.open({
              title: '提示'
              , content: '上传成功',
              btn: ["确定"],
              btn1: function (index, layero) {
                location.reload();
              },
            });
          }
        },
        error: function () {
          layer.open({
            title: '错误信息'
            , content: '上传出错，请联系管理员'
          });
        }
      })
      //  提交时间
    },
    cancel: function (layero, index) {
      layer.closeAll();
    }
  });
})

$("#add").on("click", function () {
  layer.open({
    type: 1,
    area: ['500px', '650px'],
    title: '添加考勤记录'
    , content: $("#whiteList"),
    shade: 0,
    btn: ['提交']
    , btn1: function (index, layero) {
      //  提交时间
      var upData = {
        liveUserId: $("#liveUserId").val(),
        liveLimitDate: "",
        liveStartHour: parseInt($("#liveStartHour").val()),
        liveEndHour: parseInt($("#liveEndHour").val()),
        addWhiteList: $("#addWhiteList").is(":checked"),
        platform: parseInt($("#infoPlatform").val()),
      }
      console.log(upData);
      var verification = true
      if (!upData.addWhiteList) {
        if ($("#openLiveDate").val() === "") {
          layer.open({
            title: '提示'
            , content: '没有填写开播日期',
            btn: ["确定"],
            btn1: function (index, layero) {
              layer.close(index);
            }
          })
          verification = false
        }
        if (upData.liveStartHour !== 0) {
          if (upData.liveEndHour <= upData.liveStartHour) {
            layer.open({
              title: '提示'
              , content: '下播时间需要大于开播时间',
              btn: ["确定"],
              btn1: function (index, layero) {
                layer.close(index);
              }
            });
            verification = false
          }
        }


      }
      if (upData.liveUserId === "") {
        layer.open({
          title: '提示'
          , content: '没有填写主播ID',
          btn: ["确定"],
          btn1: function (index, layero) {
            layer.close(index);
          }
        })
        verification = false
      }
      console.log(verification);
      upData.liveLimitDate = new Date($("#openLiveDate").val()).getTime()

      if (verification) {
        $.post({
          url: window.ioApi.liveUser.addLiveLimitList,
          data: upData,
          success: function (res) {
            console.log(res);
            if (res.code === 510) {
              layer.open({
                title: '提示'
                , content: '主播ID不存在或者不是该平台的主播',
                btn: ["确定"],
                btn1: function (index, layero) {
                  layer.close(index);
                }
              });
            } else if (res.code === 511) {
              layer.open({
                title: '提示'
                , content: '上传参数错误',
                btn: ["确定"],
                btn1: function (index, layero) {
                  layer.close(index);
                }
              });
            }

            else {
              layer.open({
                title: '提示'
                , content: '上传成功',
                btn: ["确定"],
                btn1: function (index, layero) {
                  location.reload();
                }
              });
            }
          }
        })
      }

    },
    cancel: function (layero, index) {
      layer.closeAll();
    }

  });

})


layui.use(['upload', 'element', 'layer'], function () {
  var $ = layui.jquery
    , upload = layui.upload
    , element = layui.element
    , layer = layui.layer;

  upload.render({
    elem: '#test8'
    , url: '' //此处配置你自己的上传接口即可
    , auto: false
    //,multiple: true
    , bindAction: '#test9'
    , accept: "file"
    , done: function (res) {
      layer.msg('上传成功');
      console.log(res)
    }
  });
})



$("#excel").on("click", function () {
  var aoa = [
    ['日期', '开播账号', "主播昵称",
      "主播ID", "上播时间", "下播时间",
      "主播平台", "是否加白"
    ],
  ];
  for (var i = 0; i < exportData.length; i++) {
    var newDataArr = [];
    newDataArr.push(exportData[i].limitDate);
    newDataArr.push(exportData[i].account);
    newDataArr.push(exportData[i].displayName);
    newDataArr.push(exportData[i].liveUserId);
    newDataArr.push(exportData[i].liveStartHour);
    newDataArr.push(exportData[i].liveEndHour);
    newDataArr.push(exportData[i].platform);
    newDataArr.push(exportData[i].addWhiteList);
    aoa.push(newDataArr)
  }
  var sheet = XLSX.utils.aoa_to_sheet(aoa);
  openDownloadDialog(sheet2blob(sheet), '开播限制录入记录 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
})



layui.use('laydate', function () {
  var laydate = layui.laydate;
  laydate.render({
    elem: '#test2',
    value: fun_date(0)
  });
});



init()

function init () {
  var limitDate = new Date(fun_date(0) + " 00:00:00").getTime()
  var platform = parseInt($("#searchPlatform").val())
  getDate({ limitDate, platform })
}


function getDate (data) {
  $.get({
    url: window.ioApi.liveUser.requestLiveLimitList,
    data: data,
    success: function (res) {
      console.log(res)
      // initTimeLine(res)
      clockOnList(res)
    }
  })
}

$("#search").on("click", function () {
  var limitDate = new Date($("#test2").val() + " 00:00:00").getTime()
  var platform = parseInt($("#searchPlatform").val())
  getDate({ limitDate, platform })
})





function isNoDataObj (obj) {
  for (var key in obj) {
    return false;
  }
  return true;
}

var portData
function clockOnList (res) {
  var data = initTable(res)
  portData = initTable(res)
  layui.use('table', function () {
    var table = layui.table;
    table.render({
      elem: '#clockOn'
      , data: data.list
      , title: ""
      , limit: 24
      , loading: false
      , cellMinWidth: 150
      , cols: [data.cols],
      id: 'testReload'
    });

  });
}

function initTable (res) {
  portData = res
  var data = res.data
  var max = res.maxLiveUserNum
  var cols = []
  cols.push({ field: 'time', title: '日期' })
  for (let i = 0; i < max; i++) {
    var item = { field: "", title: "" }
    item.field = "liveUser" + i
    item.title = "主播" + (i + 1)
    cols.push(item)
  }
  var list = []
  for (let i = 0; i < data.length; i++) {
    var d = {}
    d.time = data[i].time
    for (let x = 0; x < data[i].userInfos.length; x++) {
      d["liveUser" + x] = data[i].userInfos[x].displayName + "(" + data[i].userInfos[x].liveUserId + ")"
    }
    list.push(d)
  }
  return { list, cols }
}


$("#liveUserExcel").on("click", function () {
  var title = []
  var data = portData.list
  for (var i = 0; i < portData.cols.length; i++) {
    title.push(portData.cols[i].title)
  }
  var aoa = [];
  aoa.push(title)
  for (var i = 0; i < data.length; i++) {
    var newDataArr = [];
    for (var key in data[i]) {
      newDataArr.push(data[i][key])
    }
    aoa.push(newDataArr)
  }
  var sheet = XLSX.utils.aoa_to_sheet(aoa);
  openDownloadDialog(sheet2blob(sheet), '开播时间表 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
})