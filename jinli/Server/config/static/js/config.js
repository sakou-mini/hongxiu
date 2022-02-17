var host = document.location.hostname;
window.config={
    //图片地址
    imgUrl:location.protocol+"//"+ location.host,
    wsUrl: getWebSocket()
};

$.get({
    url:"/backOffice/service/environment",
    success:function (res) {
        console.log(res);
        switch (res) {
            case "local":
                $("title").text("锦鲤直播—本地")
                break
            case "pre":
                $("title").text("锦鲤直播—PRE")
                break
            case "prod":
                $("title").text("锦鲤直播—正式服")
                break
        }
    }
})



function checkip(ip) {
    var pattern=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
    flag_ip=pattern.test(ip);
    return flag_ip
}

function getWebSocket(){
    if(checkip(document.location.hostname)){
        return "ws://"+document.location.hostname+":7001"
    }else {
        var hostArr = document.location.hostname.split(".")
        var hsn = ""
        hostArr.forEach( (v,i) => {
            if(i!==0){
                i===hostArr.length-1? hsn += v: hsn += v+"."
            }
        },this);
        return "wss://ws."+hsn+"/api/ws/game01/"
    }
}

//数据请求接口
window.ioApi={
    liveUser:{
        getAppLiveUser:"/backOffice/liveUser/getAppLiveUserList",//获取申请主播列表
        rejectLiveUser:"/backOffice/liveUser/RejectLiveUser",//驳回主播
        passLiveUser:"/backOffice/liveUser/PassLiveUser",//通过主播
        getUserIdImage:"/backOffice/liveUser/getUserIdImageURLbyId",//获取主播身份证图片
        getPassLiveUser:"/backOffice/liveUser/getLiveUserApproveRecordList",//获取通过主播列表
        getRejectLiveUser:"/backOffice/liveUser/getRejectLiveUserList",//获取未通过主播列表
        getLiveUserList:"/backOffice/liveUser/getLiveUserList",//获取主播列表
        becomeLiveUser:"/backOffice/liveUser/becomeLiveUser",//成为主播
        unLockLiveList:"/backOffice/liveUser/unLockLiveList",//批量解封主播
        queryBanLiveUser:"/backOffice/liveUser/queryBanLiveUser",//查询封禁主播
        closeLive:"/backOffice/liveMonitoring/closeLive",
        muteChatRequest:"/backOffice/liveMonitoring/muteChatRequest",
        unMuteChat:"/backOffice/liveMonitoring/unMuteChat",
        updateRoomHot:"/backOffice/liveUser/updateRoomHot",
        getLiveUrl:"/share/getLiveUrl",
        getRoomList:"/backOffice/liveUser/getRoomList",
        swapRoomRecommendSort:"/backOffice/liveUser/swapRoomRecommendSort",
        getLiveRecordChartData:"/backOffice/liveUser/getLiveRecordChartData",
        getLiveSourceLineList:"/backOffice/liveUser/getLiveSourceLineList",
        openOrCloseLiveSourceLine:"/backOffice/liveUser/openOrCloseLiveSourceLine",
        queryLiveAttendance:"/backOffice/liveUser/queryLiveAttendance",
        getLiveUserSummary:"/backOffice/liveUser/getLiveUserSummary",
        getLiveUserPageList:"/backOffice/liveUser/getLiveUserPageList",
        queryLiveRecord:"/backOffice/liveUser/queryLiveRecord",
        unLockLiveUser:"/backOffice/liveUser/unLockLiveUser",
        banLiveUser:"/backOffice/liveUser/banLiveUser",
        changePlatform:"/backOffice/liveUser/changePlatform",
        uploadLiveLimitExcel:"/backOffice/liveUser/uploadLiveLimitExcel",
        addLiveLimitList:"/backOffice/liveUser/addLiveLimitList",
        requestLiveLimitAddRecord:"/backOffice/liveUser/requestLiveLimitAddRecord",
        requestLiveLimitList:"/backOffice/liveUser/requestLiveLimitList",
        queryPlatformLiveRecordInfo:"/backOffice/liveUser/queryPlatformLiveRecordInfo",
        updateBanLivePermission:"/backOffice/liveUser/updateBanLivePermission",
        updateLiveUserSharedPlatform:"/backOffice/liveUser/updateLiveUserSharedPlatform"

    },
    report:{
        getReport:"/backOffice/liveUser/getAllReportList",//获取举报列表
        getReportByConditionQuery:"/backOffice/liveUser/fuzzyQueryReportList",//多条件模糊查询举报
        getReportByConditionQueryNotType:"/backOffice/liveUser/fuzzyQueryReportListNotType",//无类型多条件查询举报
        handleReport:"/backOffice/liveUser/handleReportById",//处理举报
    },
    personDiary:{
        getPersonDiary:"/backOffice/personDiary/getPersonDiaryByStatue",//根据状态获取动态列表
        getPersonDiaryByUserIdAndTime:"/backOffice/personDiary/getPersonDiaryByUserIdAndTime",//根据id和时间查询动态
        getPassPersonDiary:"/backOffice/personDiary/getPersonDiaryApprovedList",//模糊查询通过动态列表
        getRejectPersonDiary:"/backOffice/personDiary/getPersonDiaryRejectList",//模糊查询不通过动态列表
        OperationPersonDiary:"/backOffice/personDiary/OperationPersonDiary",//处理未审核的动态
        recommendDiaryDetail:"/backOffice/personDiary/recommendDiaryDetail",
        recommend:"/backOffice/personDiary/recommend",
        cancelRecommend:"/backOffice/personDiary/cancelRecommend",
        diaryList:"/backOffice/personDiary/diaryList",
    },
    guess:{
        getGuessByTime:"/backOffice/guess/getGuessByTime",//获取竞猜展示列表
        getNotStartGuess:"/backOffice/guess/getNotStartGuess",//获取尚未开始竞猜展示列表
        getShowOverGuess:"/backOffice/guess/getShowOverGuess",//获取展示结束竞猜列表
        getGuessStatisticsList:"/backOffice/guess/lottery/getGuessStatisticsList",//获取竞猜数据统计
        addNewGuess:"/backOffice/guess/lottery/addNewGuess",//新增竞猜
        getGuessWagerListByGuessId:"/backOffice/guess/lottery/getGuessWagerListByGuessId",//根据竞猜id获取下注记录
        getWagerOverGuess:"/backOffice/guess/getWagerOverGuess",//获取下注结束竞猜
        getWaitPrizeList:"/backOffice/guess/lottery/getWaitPrizeList",//获取待开奖竞猜
        guessEdit:"/backOffice/guess/lottery/guessEdit",//修改竞猜
        drawPrize:"/backOffice/guess/lottery/drawPrize",//竞猜开奖
    },
    backOffice:{
        addBackOffice:"/backOffice/backOfficeUser/admin/addBackOffice",//添加管理员
        getBackOfficeUserList:"/backOffice/backOfficeUser/admin/getBackOfficeUserList",//获取管理员列表
        editBackOffice:"/backOffice/backOfficeUser/admin/editBackOffice",//修改管理员权限
        getBackOfficeMsg:"/backOffice/getBackOfficeMsg",//获取登录管理员信息
        getServerInfo:"/backOffice/backOfficeUser/admin/getServerInfo",//获取服务器信息
        serverClose:"/backOffice/backOfficeUser/admin/serverClose",//关闭服务器
        serverOpen:"/backOffice/backOfficeUser/admin/serverOpen",//开启服务器
        domainRecordInfo:"/backOffice/backOfficeUser/domainRecordInfo",
        updateDomainName:"/backOffice/backOfficeUser/updateDomainName",
        domainList:"/backOffice/backOfficeUser/domainList",
        addDomain:"/backOffice/backOfficeUser/addDomain",
        removeDomain:"/backOffice/backOfficeUser/removeDomain",
        modifyPassword:"/backOffice/backOfficeUser/admin/modifyPassword",
        getAllMenuRoles:"/backOffice/backOfficeUser/admin/getAllMenuRoles",
        liveDomainList:"/backOffice/backOfficeUser/liveDomainList",
        updateLiveDomain:"/backOffice/backOfficeUser/updateLiveDomain",
        liveDomainRecord:"/backOffice/backOfficeUser/liveDomainRecord",
    },
    officialLive:{
        addOfficialLive:"/backOffice/officialLive/addOfficialLive",//添加官方直播间
        getOfficialLiveList:"/backOffice/officialLive/getOfficialLiveList",//获取正在直播列表
        closeOfficialLive:"/backOffice/officialLive/closeOfficialLive",//关闭直播间
        getCloseOfficialList:"/backOffice/officialLive/getCloseOfficialList",//获取历史直播间列表
    },
    user:{
        getUserList:"/backOffice/user/getUserList",//获取用户列表
        operationUserCoin:"/backOffice/user/operationUserCoin",//操作用户金币
        getRecordList:"/backOffice/user/getRecordList",//获取金币操作列表
        getNewUserChartData:"/backOffice/user/getNewUserChartData",//获取新用户报表数据
        getActiveUserChartData:"/backOffice/user/getActiveUserChartData",//获取活跃用户报表数据
        getRetainedUserChartData:"/backOffice/user/getRetainedUserChartData",//获取用户留存报表数据
        getMobileDevicesChartData:"/backOffice/user/getMobileDevicesChartData",//获取手机型号报表数据
        onlineDataSummary:"/backOffice/userData/onlineDataSummary",//获取用户总数量与目前在线人数
        currentMonthDataSummary:"/backOffice/userData/currentMonthDataSummary",//获取当月数据
        todayUserDataSummary:"/backOffice/userData/todayUserDataSummary",//获取当日数据
        monthUserDataDetail:"/backOffice/userData/monthUserDataDetail",//获取一年内每月数据
        todayUserDataDetail:"/backOffice/userData/todayUserDataDetail",//获取当天礼物信息

        platformIndexData:"/backOffice/userData/platformIndexData",
        getGroupMaxActiveDayData:"/backOffice/user/getGroupMaxActiveDayData",//获取活跃分布数据
        getNearestRetainedUserReportData:"/backOffice/user/getNearestRetainedUserReportData",//获取昨日留存数据
        banUser:"/backOffice/user/banUser",//拉黑用户
        unsealedUser:"/backOffice/user/unsealedUser",//解封用户
        changePassword:"/backOffice/user/changePassword",//修改密码
        passwordRecord:"/backOffice/user/passwordRecord",
        queryBanUserList:"/backOffice/user/queryBanUserList",
        queryUserBetDetail:"/backOffice/user/queryUserBetDetail",


        queryPlatformUserOnlineInfo:"/backOffice/user/queryPlatformUserOnlineInfo",
        queryPlatformDayUserTotalData:"/backOffice/user/queryPlatformDayUserTotalData",
        queryUserListForTotal:"/backOffice/user/queryUserListForTotal",
        userLiveWatchRecord:"/backOffice/user/userLiveWatchRecord",
        dailyUserActive:"/backOffice/user/dailyUserActive"
    },
    revenueAnalysis:{
        getGiftRecords:"/backOffice/revenueAnalysis/getGiftRecords",//获取礼物消费记录
        getGiftIncomeRecords:"/backOffice/revenueAnalysis/getGiftIncomeRecords",//获取主播收礼记录
        getPageRecordList:"/backOffice/revenueAnalysis/getPageRecordList",
        getBetRecordSummary:"/backOffice/revenueAnalysis/getBetRecordSummary",

    },
    service:{
        feedbackPageInfo:"/backOffice/service/feedbackPageInfo",
    },
    gift:{
        giftPageInfo:"/backOffice/gift/giftPageInfo",
        changePrice:"/backOffice/gift/changePrice",
        openOrCloseGift:"/backOffice/gift/openOrCloseGift",
    },
    liveMonitoring:{
        audienceList:"/backOffice/liveMonitoring/audienceList",
    },
    platform:{
        getRechargeRecord:"/backOffice/platform/getRechargeRecord",
        getLiveUserPageList:"/backOffice/platform/getLiveUserPageList",
    },
    backOfficeMessage:{
        addCarousel:"/backOffice/backOfficeMessage/addCarousel",
        updateCarousel:"/backOffice/backOfficeMessage/updateCarousel",
        findCarousel:"/backOffice/backOfficeMessage/findCarousel",
        getRaceFree:"/backOffice/backOfficeMessage/getRaceFree",
        getCarouselData:"/backOffice/backOfficeMessage/getCarouselData",
        removeCarousel:"/backOffice/backOfficeMessage/removeCarousel",
        tipsMessage:"/backOffice/backOfficeMessage/tipsMessage",
        sendRollMessage:"/backOffice/backOfficeMessage/sendRollMessage",
        tipMessageRecord:"/backOffice/backOfficeMessage/tipMessageRecord",
        rollMessageRecord:"/backOffice/backOfficeMessage/rollMessageRecord",
        cleanRollMessage:"/backOffice/backOfficeMessage/cleanRollMessage",
        platformMessage:"/backOffice/backOfficeMessage/platformMessage"
    },
    userData:{
        todayLiveTotalData:"/backOffice/userData/todayLiveTotalData",
        queryLiveTotalData:"/backOffice/userData/queryLiveTotalData"
    }
};


//跳转路径
window.pathUrl=[
    //index


    {url:"/backOffice/home/index",name:"index"},
    {url:"/backOffice/home/overviewToday",name:"overviewToday"},
    {url:"/backOffice/home/liveDataSummary",name:"liveDataSummary"},
    {url:"/backOffice/home/userDataSummary",name:"userDataSummary"},
    //LiveUser
    {url:"/backOffice/liveUser/LiveUserExamine",name:"LiveUserExamine"},
    {url:"/backOffice/liveUser/rejectLiveUser",name:"rejectLiveUser"},
    {url:"/backOffice/liveUser/approvedLiveUser",name:"approvedLiveUser"},
    {url:"/backOffice/liveUser/liveUserList",name:"liveUserList"},
    {url:"/backOffice/guess/liveUserRoom",name:"liveUserRoom"},
    {url:"/backOffice/liveUser/liveUserDetails",name:"liveUserDetails"},
    {url:"/backOffice/liveUser/liveDetails",name:"liveDetails"},
    {url:"/backOffice/liveUser/liveUserRecord",name:"liveUserRecord"},
    {url:"/backOffice/liveUser/liveRecommend",name:"liveRecommend"},
    {url:"/backOffice/liveUser/liveUserDetailInfo",name:"liveUserDetailInfo"},
    {url:"/backOffice/liveUser/liveRecord",name:"liveRecord"},

    {url:"/backOffice/liveUser/reportDetails",name:"reportDetails"},
    {url:"/backOffice/liveUser/liveUserAttendance",name:"liveUserAttendance"},
    {url:"/backOffice/liveUser/liveSourceLine",name:"liveSourceLine"},
    {url:"/backOffice/liveUser/banLiveUserList",name:"banLiveUserList"},
    {url:"/backOffice/liveUser/liveUserClockOn",name:"liveUserClockOn"},
    //personDiary
    {url:"/backOffice/liveUser/personDiaryList",name:"personDiaryList"},
    {url:"/backOffice/liveUser/personDiaryReject",name:"personDiaryReject"},
    {url:"/backOffice/liveUser/personDiaryApproved",name:"personDiaryApproved"},
    {url:"/backOffice/liveUser/personDiaryDetails",name:"personDiaryDetails"},
    //guess
    {url:"/backOffice/backOfficeUser/guessList",name:"guessList"},
    {url:"/backOffice/backOfficeUser/lottery/guessDetails",name:"guessDetails"},
    {url:"/backOffice/backOfficeUser/guessNotStart",name:"guessNotStart"},
    {url:"/backOffice/backOfficeUser/lottery/guessStatistics",name:"guessStatistics"},
    {url:"/backOffice/backOfficeUser/guessShowOver",name:"guessShowOver"},
    {url:"/backOffice/backOfficeUser/guessWagerOver",name:"guessWagerOver"},
    {url:"/backOffice/backOfficeUser/lottery/guessWaitPrize",name:"guessWaitPrize"},
    {url:"/backOffice/backOfficeUser/lottery/guessAdditions",name:"guessAdditions"},
    {url:"/backOffice/backOfficeUser/lottery/guessDrawPrize",name:"guessDrawPrize"},
    {url:"/backOffice/backOfficeUser/lottery/guessWagerDetailsList",name:"guessWagerDetailsList"},
    {url:"/backOffice/backOfficeUser/lottery/guessStatisticsDetails",name:"guessStatisticsDetails"},
    {url:"/backOffice/guess/liveUserListJinLi",name:"liveUserListJinLi"},
    {url:"/backOffice/guess/liveUserJinLiDetailInfo",name:"liveUserJinLiDetailInfo"},
    {url:"/backOffice/guess/indexJinli",name:"indexJinli"},
    //backOfficeUser
    {url:"/backOffice/backOfficeUser/admin/backOfficeManagement",name:"backOfficeManagement"},
    {url:"/backOffice/backOfficeUser/admin/backOfficeUserAdditions",name:"backOfficeUserAdditions"},
    {url:"/backOffice/backOfficeUser/admin/backOfficeUserEdit",name:"backOfficeUserEdit"},
    {url:"/backOffice/backOfficeUser/admin/theServer",name:"theServer"},
    {url:"/backOffice/backOfficeUser/admin/changePasswordRecord",name:"changePasswordRecord"},
    {url:"/backOffice/backOfficeUser/domainNameSetQ",name:"domainNameSetQ"},
    {url:"/backOffice/backOfficeUser/domainNameSetRecordQ",name:"domainNameSetRecordQ"},
    {url:"/backOffice/backOfficeUser/reportList",name:"reportList"},

    //officialLive
    {url:"/backOffice/backOfficeUser/officialLiveList",name:"officialLiveList"},
    {url:"/backOffice/backOfficeUser/newOfficialLive",name:"newOfficialLive"},
    {url:"/backOffice/backOfficeUser/officialLiveRecords",name:"officialLiveRecords"},
    //user
    {url:"/backOffice/user/userList",name:"userList"},
    {url:"/backOffice/user/coinOperationRecord",name:"coinOperationRecord"},
    {url:"/backOffice/revenueAnalysis/newUserChart",name:"newUserChart"},
    {url:"/backOffice/revenueAnalysis/activeUserChart",name:"activeUserChart"},
    {url:"/backOffice/revenueAnalysis/retainedUserChart",name:"retainedUserChart"},
    {url:"/backOffice/revenueAnalysis/conversionRatesChart",name:"conversionRatesChart"},
    {url:"/backOffice/revenueAnalysis/mobileDevicesChart",name:"mobileDevicesChart"},
    {url:"/backOffice/user/taskAnalysis",name:"taskAnalysis"},
    {url:"/backOffice/user/userDetails",name:"userDetails"},
    {url:"/backOffice/user/userData",name:"userData"},
    {url:"/backOffice/user/monthlyDataDetails",name:"monthlyDataDetails"},
    {url:"/backOffice/user/sameDayDataDetails",name:"sameDayDataDetails"},
    {url:"/backOffice/user/userBetRecord",name:"userBetRecord"},
    {url:"/backOffice/user/banUserList",name:"banUserList"},
    {url:"/backOffice/user/viewingRecords",name:"viewingRecords"},
    {url:"/backOffice/user/userReport",name:"userReport"},
    //gift
    {url:"/backOffice/gift/giftList",name:"giftList"},
    {url:"/backOffice/gift/giftConsumptionList",name:"giftConsumptionList"},
    {url:"/backOffice/gift/liveUserReceivedGiftList",name:"liveUserReceivedGiftList"},
    //revenueAnalysis

    {url:"/backOffice/revenueAnalysis/betsRecordList",name:"betsRecordList"},
    {url:"/backOffice/revenueAnalysis/revenueData",name:"revenueData"},
    {url:"/backOffice/revenueAnalysis/paymentPenetration",name:"paymentPenetration"},
    {url:"/backOffice/revenueAnalysis/newPlayerValue",name:"newPlayerValue"},
    {url:"/backOffice/revenueAnalysis/paymentHabit",name:"paymentHabit"},
    {url:"/backOffice/revenueAnalysis/revenueIndex",name:"revenueIndex"},
    //backOfficeMessage
    {url:"/backOffice/guess/sendBackOfficeMessage",name:"sendBackOfficeMessage"},
    {url:"/backOffice/guess/messageSettings",name:"messageSettings"},
    {url:"/backOffice/guess/sendBackOfficeMessageRecord",name:"sendBackOfficeMessageRecord"},
    {url:"/backOffice/guess/messageSettingsRecord",name:"messageSettingsRecord"},
    {url:"/backOffice/guess/feedback",name:"feedback"},
    {url:"/backOffice/backOfficeUser/guessCarousel",name:"guessCarousel"},
    {url:"/backOffice/backOfficeUser/liveCarousel",name:"liveCarousel"},
    {url:"/backOffice/backOfficeUser/matchCarousel",name:"matchCarousel"},
    {url:"/backOffice/backOfficeUser/addGuessCarousel",name:"addGuessCarousel"},
    {url:"/backOffice/backOfficeUser/addLiveCarousel",name:"addLiveCarousel"},
    {url:"/backOffice/backOfficeUser/addMatchCarousel",name:"addMatchCarousel"},
    //platformList
    {url:"/backOffice/platformT/indexT",name:"indexT"},
    {url:"/backOffice/platformT/liveUserListT",name:"liveUserListT"},
    {url:"/backOffice/platformT/liveRoomListT",name:"liveRoomListT"},
    {url:"/backOffice/platformT/liveUserDetailsT",name:"liveUserDetailsT"},
    {url:"/backOffice/platformT/liveDetailsT",name:"liveDetailsT"},
    {url:"/backOffice/platformT/liveUserApplyT",name:"liveUserApplyT"},
    {url:"/backOffice/platformT/liveUserAuditRecordsT",name:"liveUserAuditRecordsT"},
    {url:"/backOffice/platformT/userListT",name:"userListT"},
    {url:"/backOffice/platformT/giftConsumptionListT",name:"giftConsumptionListT"},
    {url:"/backOffice/platformT/liveUserRecordT",name:"liveUserRecordT"},
    {url:"/backOffice/platformT/liveUserDetailInfoT",name:"liveUserDetailInfoT"},
    {url:"/backOffice/platformT/userDetailsT",name:"userDetailsT"},
    {url:"/backOffice/platformT/messageSettingsT",name:"messageSettingsT"},
    {url:"/backOffice/platformT/receivedGiftListT",name:"receivedGiftListT"},
    {url:"/backOffice/platformT/backOfficeMessageT",name:"backOfficeMessageT"},
    {url:"/backOffice/platformT/rechargeRecordT",name:"rechargeRecordT"},
    {url:"/backOffice/platformT/platformLiveUserDetails",name:"platformLiveUserDetails"},
    {url:"/backOffice/platformT/banLiveUserListT",name:"banLiveUserListT"},
    {url:"/backOffice/platformT/giftListT",name:"giftListT"},
    {url:"/backOffice/platformT/domainNameSetT",name:"domainNameSetT"},
    {url:"/backOffice/platformT/domainNameSetRecordT",name:"domainNameSetRecordT"},
    {url:"/backOffice/platformT/backOfficeMessageRecordT",name:"backOfficeMessageRecordT"},
    {url:"/backOffice/platformT/messageSettingsRecordT",name:"messageSettingsRecordT"},

    {url:"/backOffice/platformQ/indexQ",name:"indexQ"},
    {url:"/backOffice/platformQ/liveUserListQ",name:"liveUserListQ"},
    {url:"/backOffice/platformQ/liveRoomListQ",name:"liveRoomListQ"},
    {url:"/backOffice/platformQ/liveUserDetailsQ",name:"liveUserDetailsQ"},
    {url:"/backOffice/platformQ/liveDetailsQ",name:"liveDetailsQ"},
    {url:"/backOffice/platformQ/liveUserApplyQ",name:"liveUserApplyQ"},
    {url:"/backOffice/platformQ/liveUserAuditRecordsQ",name:"liveUserAuditRecordsQ"},
    {url:"/backOffice/platformQ/userListQ",name:"userListQ"},
    {url:"/backOffice/platformQ/giftConsumptionListQ",name:"giftConsumptionListQ"},
    {url:"/backOffice/platformQ/liveUserRecordQ",name:"liveUserRecordQ"},
    {url:"/backOffice/platformQ/liveUserDetailInfoQ",name:"liveUserDetailInfoQ"},
    {url:"/backOffice/platformQ/userDetailsQ",name:"userDetailsQ"},
    {url:"/backOffice/backOfficeUser/messageSettingsQ",name:"messageSettingsQ"},
    {url:"/backOffice/platformQ/receivedGiftListQ",name:"receivedGiftListQ"},
    {url:"/backOffice/backOfficeUser/backOfficeMessageQ",name:"backOfficeMessageQ"},
    {url:"/backOffice/platformQ/rechargeRecordQ",name:"rechargeRecordQ"},
    {url:"/backOffice/platformQ/banLiveUserListQ",name:"banLiveUserListQ"},
    {url:"/backOffice/platformQ/giftListQ",name:"giftListQ"},

    {url:"/backOffice/backOfficeUser/backOfficeMessageRecordQ",name:"backOfficeMessageRecordQ"},
    {url:"/backOffice/backOfficeUser/messageSettingsRecordQ",name:"messageSettingsRecordQ"},
    {url:"/backOffice/backOfficeUser/liveDomainNameSet",name:"liveDomainNameSet"},


];

//管理员列表
window.backOfficeRole=[
    {name:"冻结账号",value:"LOGIN"},
    {name:"超级管理员",value:"ADMIN"},

    {name:"用户操作",value:"USEROPT"},
    {name:"开奖竞猜",value:"LOTTERY"},
];
window.platformRole=[
    {name:"Q平台API",value:"PLATFORM_Q"},
    {name:"T平台API",value:"PLATFORM"},
]
window.menuRole=[
    {name:"用户管理",value:"USER"},
    {name:"收入分析",value:"REVENUEANALYSIS"},
    {name:"主播管理",value:"LIVEUSER"},
    {name:"系统管理",value:"BACKOFFICEMESSAGE"},
    {name:"礼物管理",value:"GIFT"},
    {name:"官方游戏",value:"GUESS"},
    {name:"T平台",value:"BACKOFFICE_T"},
    {name:"Q平台",value:"BACKOFFICE_Q"},
]


//游戏选项配置
window.gameType=[
    {name:"百家乐",type:"BACCARAT",value:"1",banker:"HAS"},
    {name:"百人牛牛",type:"NIUNIU",value:"2",banker:"HAS"},
    {name:"炸金花",type:"GOLDENFLOWER",value:"3",banker:"HAS"},
    {name:"红黑大战",type:"REDBLACK",value:"4",banker:"HAS"},
    {name:"龙虎斗",type:"LONGHU",value:"5",banker:"NOT"},
    {name:"交友",type:"JIAOYOU",value:"1000",banker:"EMPTY"},
    {name:"萌宠",type:"MENGCHONG",value:"1001",banker:"EMPTY"},
    {name:"颜值",type:"YANZHI",value:"1002",banker:"EMPTY"},
    {name:"跳舞",type:"TIAOWU",value:"1003",banker:"EMPTY"},
    {name:"唱歌",type:"CHANGGE",value:"1004",banker:"EMPTY"},
];

window.gift=[
    {id:"10000",name:"强吻",type:"普通礼物"},
    {id:"10008",name:"小爱心",type:"普通礼物"},
    {id:"10009",name:"钻石包",type:"普通礼物"},
    {id:"10010",name:"小钻戒",type:"普通礼物"},
    {id:"10011",name:"香槟",type:"普通礼物"},
    {id:"10012",name:"红包雨",type:"普通礼物"},
    {id:"10013",name:"热气球",type:"普通礼物"},
    {id:"10014",name:"许愿瓶",type:"普通礼物"},
    {id:"10015",name:"大摩托",type:"普通礼物"},
    {id:"20004",name:"城堡",type:"豪华礼物"},
    {id:"20005",name:"女孩",type:"豪华礼物"},
    {id:"20006",name:"大钞票枪",type:"豪华礼物"},
];


window.betType=[
    {name:"龙",type:"LONG"},
    {name:"虎",type:"HU"},
    {name:"和",type:"LONGHU_DRAW"},
    {name:"庄",type:"BACCARAT_DEALER"},
    {name:"闲",type:"BACCARAT_PLAYER"},
    {name:"和",type:"BACCARAT_DRAW"},
    {name:"庄对",type:"BACCARAT_DEALER_PAIR"},
    {name:"闲对",type:"BACCARAT_PLAYER_PAIR"},
    {name:"红桃",type:"SPADE_AREA"},
    {name:"黑桃",type:"HEART_AREA"},
    {name:"梅花",type:"CLUB_AREA"},
    {name:"方片",type:"DIAMOND_AREA"},
    {name:"红",type:"RED"},
    {name:"黑",type:"BLACK"},
    {name:"幸运一击",type:"RED_BLACK_LUCk"}

]

window.brand = [
    {name:"其他",type:"BRAND_OTHER",num:"0"},
    {name:"苹果",type:"BRAND_IPHONE",num:"1"},
    {name:"华为",type:"BRAND_HUAWEI",num:"2"},
    {name:"OPPO",type:"BRAND_OPPO",num:"3"},
    {name:"VIVO",type:"BRAND_VIVO",num:"4"},
    {name:"小米",type:"BRAND_XIAOMI",num:"5"},
    {name:"魅族",type:"BRAND_MEIZU",num:"6"},
    {name:"三星",type:"BRAND_SAMSUNG",num:"7"},
    {name:"一加",type:"BRAND_ONEPLUS",num:"8"},
    {name:"真我",type:"BRAND_REALME",num:"9"},
]

window.liveSourceLine = [
    {name:"网宿科技",type:"WANGSU_LINE"},
    {name:"腾讯云",type:"TENCENT_LINE"},
    {name:"阿里云",type:"ALIYUN_LINE"},
    {name:"RTMP服务器",type:"NGINX_LINE"}
]

function hasRole(role,roleList) {
    var fl = false
    for(let i=0;i<roleList.length;i++){
        if(roleList[i] === role){
            fl = true
        }
    }
    return fl
}


function roleInfo(hasRoleList) {
    var roleData =[{
        checked:hasRole("INDEX",hasRoleList),
        id:"INDEX",
        title:"首页"
    },{
        checked:hasRole("USER",hasRoleList),
        id:"USER",
        title: '用户管理' //一级菜单
        ,children: [{
            checked:hasRole("USERLIST",hasRoleList),
            id:"USERLIST",
            title: '用户列表' //二级菜单
            ,children: [{
                checked:hasRole("OPERATIONUSERCOIN",hasRoleList),
                id:"OPERATIONUSERCOIN",
                title: '修改金币'
            },{
                checked:hasRole("BANUSER",hasRoleList),
                id:"BANUSER",
                title: '拉黑用户'
            },{
                checked:hasRole("BECOMELIVEUSER",hasRoleList),
                id:"BECOMELIVEUSER",
                title: '成为主播'
            },{
                checked:hasRole("CHANGEPASSWORD",hasRoleList),
                id:"CHANGEPASSWORD",
                title: '修改密码'
            },{
                checked:hasRole("UNSEALEDUSER",hasRoleList),
                id:"UNSEALEDUSER",
                title: '解封用户'
            }]
        },{
            checked:hasRole("BANUSERLIST",hasRoleList),
            id:"BANUSERLIST"  ,
            title: '封禁用户',
        },{
            checked:hasRole("COINOPERATIONRECORD",hasRoleList),
            id:"COINOPERATIONRECORD",
            title: '金币修改记录',
        }]
    },{
        checked:hasRole("REVENUEANALYSIS",hasRoleList),
        id:"REVENUEANALYSIS",
        title: '收入分析' //一级菜单
    },{
        checked:hasRole("LIVEUSER",hasRoleList),
        id:"LIVEUSER",
        title: '主播管理' ,
        children:[{
            checked:hasRole("LIVESOURCELINE",hasRoleList),
            id:"LIVESOURCELINE",
            title:"CDN管理"
        }]
    },{
        checked:hasRole("BACKOFFICEMESSAGE",hasRoleList),
        id:"BACKOFFICEMESSAGE",
        title: '系统管理' ,
        children:[{
            checked:hasRole("SENDBACKOFFICEMESSAGE",hasRoleList),
            id:"SENDBACKOFFICEMESSAGE",
            title:"走马灯消息"
        },{
            checked:hasRole("MESSAGESETTINGS",hasRoleList),
            id:"MESSAGESETTINGS",
            title:"直播公告"
        },{
            checked:hasRole("CAROUSEL",hasRoleList),
            id:"CAROUSEL",
            title:"轮播图设置"
        }]
    },{
        checked:hasRole("GIFT",hasRoleList),
        id:"GIFT",
        title: '礼物管理' ,
    },{
        checked:hasRole("GUESS",hasRoleList),
        id:"GUESS",
        title: '官方游戏' ,
    },{
        checked:hasRole("BACKOFFICE_T",hasRoleList),
        id:"BACKOFFICE_T",
        title: 'T平台' ,
        children:[{
            checked:hasRole("LIVEROOMLISTT",hasRoleList),
            id:"LIVEROOMLISTT",
            title:"直播监控"
        },{
            checked:hasRole("LIVEUSERLISTT",hasRoleList),
            id:"LIVEUSERLISTT",
            title:"主播列表",
            children:[
                {
                    checked:hasRole("LIVEUSERDETAILINFOT",hasRoleList),
                    id:"LIVEUSERDETAILINFOT",
                    title:"主播详情",
                }
            ]
        },{
            checked:hasRole("BANLIVEUSERLISTT",hasRoleList),
            id:"BANLIVEUSERLISTT",
            title:"封禁主播列表"
        },{
            checked:hasRole("LIVEUSERAPPLYT",hasRoleList),
            id:"LIVEUSERAPPLYT",
            title:"主播审核"
        },{
            checked:hasRole("USERLISTT",hasRoleList),
            id:"USERLISTT",
            title:"用户列表"
        },{
            checked:hasRole("LIVEUSERRECORDT",hasRoleList),
            id:"LIVEUSERRECORDT",
            title:"直播记录"
        },{
            checked:hasRole("RECHARGERECORDT",hasRoleList),
            id:"RECHARGERECORDT",
            title:"充值记录"
        },{
            checked:hasRole("GIFTCONSUMPTIONLISTQ",hasRoleList),
            id:"GIFTCONSUMPTIONLISTQ",
            title:"礼物记录"
        },{
            checked:hasRole("GIFTLISTT",hasRoleList),
            id:"GIFTLISTT",
            title:"礼物管理"
        },{
            checked:hasRole("MESSAGESETTINGST",hasRoleList),
            id:"MESSAGESETTINGST",
            title:"消息设置"
        },{
            checked:hasRole("DOMAINNAMESETT",hasRoleList),
            id:"DOMAINNAMESETT",
            title:"T平台域名设置"
        },{
            checked:hasRole("DOMAINNAMESETRECORDT",hasRoleList),
            id:"DOMAINNAMESETRECORDT",
            title:"T平台域名操作记录"
        }]
    },{
        checked:hasRole("BACKOFFICE_Q",hasRoleList),
        title: 'Q平台' ,
        id:"BACKOFFICE_Q",
        children:[{
            checked:hasRole("LIVEROOMLISTQ",hasRoleList),
            id:"LIVEROOMLISTQ",
            title:"直播监控"
        },{
            checked:hasRole("LIVEUSERLISTQ",hasRoleList),
            id:"LIVEUSERLISTQ",
            title:"主播列表",
            children:[
                {
                    checked:hasRole("LIVEUSERDETAILINFOQ",hasRoleList),
                    id:"LIVEUSERDETAILINFOQ",
                    title:"主播详情",
                }
            ]
        },{
            checked:hasRole("BANLIVEUSERLISTQ",hasRoleList),
            id:"BANLIVEUSERLISTQ",
            title:"封禁主播列表"
        },{
            checked:hasRole("LIVEUSERAPPLYQ",hasRoleList),
            id:"LIVEUSERAPPLYQ",
            title:"主播审核"
        },{
            checked:hasRole("USERLISTQ",hasRoleList),
            id:"USERLISTQ",
            title:"用户列表"
        },{
            checked:hasRole("LIVEUSERRECORDQ",hasRoleList),
            id:"LIVEUSERRECORDQ",
            title:"直播记录"
        },{
            checked:hasRole("GIFTCONSUMPTIONLISTQ",hasRoleList),
            id:"GIFTCONSUMPTIONLISTQ",
            title:"礼物记录"
        },{
            checked:hasRole("GIFTLISTQ",hasRoleList),
            id:"GIFTLISTQ",
            title:"礼物管理"
        },{
            checked:hasRole("MESSAGESETTINGSQ",hasRoleList),
            id:"MESSAGESETTINGSQ",
            title:"消息设置"
        },{
            checked:hasRole("DOMAINNAMESETQ",hasRoleList),
            id:"DOMAINNAMESETQ",
            title:"Q平台域名设置"
        },{
            checked:hasRole("DOMAINNAMESETRECORDQ",hasRoleList),
            id:"DOMAINNAMESETRECORDQ",
            title:"Q平台域名操作记录"
        }]
    }]

    return roleData
}