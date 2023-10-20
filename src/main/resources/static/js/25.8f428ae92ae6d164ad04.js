webpackJsonp([25],{YfpU:function(e,a,t){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var i=t("Vo7i");t("mw3O");var l=t("d6/B"),s={name:"ImageManage",data:function(){return{listLoading:!1,envSelect:[],tableData:[],queryParams:{envId:"",beginTime:"",endTime:"",nameCode:"",pageNum:1,pageSize:10},rangeTime:"",pages:1,total:1,tableSelectData:[],eviewedDialog:!1,eviewedForm:{isReviewed:1},detailsDialog:!1,dialogData:{type:"details",row:{}}}},filters:{isReviewedTxt:function(e){return 0==e?"未通过审核":1==e?"通过审核":2==e?"待审核":void 0}},methods:{handleSizeChange:function(e){this.queryParams.pageSize=e,this.getList()},handleCurrentChange:function(e){this.queryParams.pageNum=e,this.getList()},queryTimeRangeHandler:function(){this.rangeTime?(this.queryParams.beginTime=this.rangeTime[0],this.queryParams.endTime=this.rangeTime[1]):(this.queryParams.beginTime="",this.queryParams.endTime="")},selectChange:function(e){this.tableSelectData=e},openDialog:function(e,a){"details"==a&&(this.dialogData={type:a,row:e},this.detailsDialog=!0)},closeDetailsDialog:function(){this.dialogData={row:{},type:"details"}},openModuleDialog:function(){0!==this.tableSelectData.length?this.eviewedDialog=!0:this.$message({type:"warning",message:"请先勾选要审核的镜像!"})},closeEviewedDialog:function(){this.$refs.appTable.clearSelection(),this.eviewedDialog=!1},searchEnvSelectChange:function(e){this.queryParams.envId=e,this.getList()},reviewImage:function(){var e,a=this,t=new FormData;this.tableSelectData.forEach(function(e){t.append("imageIds",e.id)}),t.append("envId",this.queryParams.envId),t.append("reviewCode",this.eviewedForm.isReviewed),(e=t,Object(i.a)({url:"/billing/batch/review/image",method:"post",data:e})).then(function(e){var t=e.data.code;200==t?a.$message({message:e.data.msg,type:"success"}):a.$message({message:e.data.msg,type:"error"}),200==t&&(a.eviewedDialog=!1,a.getList())})},getList:function(){var e,a=this;a.listLoading=!0,a.queryTimeRangeHandler(),(e=a.queryParams,Object(i.a)({url:"/billing/image/show",method:"get",params:e})).then(function(e){var t=e.data.data,i=e.data.code;200===i?(a.tableData=t.list,a.total=t.total,a.pages=t.pages,a.queryParams.pageNum=t.pageNum,a.queryParams.pageSize=t.pageSize):"未查询到任何数据"!=e.data.msg&&201!=i||(a.tableData=[]);setTimeout(function(){a.listLoading=!1},200)})},getEnv:function(){var e=this;Object(l.b)().then(function(a){200==a.data.code?(e.envSelect=a.data.data,0!=e.envSelect.length&&e.$nextTick(function(){e.queryParams.envId=e.envSelect[0].id,e.getList()})):e.envSelect=[]})}},created:function(){this.getEnv()}},n={render:function(){var e=this,a=e.$createElement,t=e._self._c||a;return t("div",[t("el-main",[t("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[t("span",{staticClass:"row-span-class"},[e._v("环境")]),e._v(" "),t("el-select",{staticStyle:{width:"10%"},attrs:{placeholder:"请选择",size:"small"},on:{change:e.searchEnvSelectChange},model:{value:e.queryParams.envId,callback:function(a){e.$set(e.queryParams,"envId",a)},expression:"queryParams.envId"}},e._l(e.envSelect,function(e){return t("el-option",{key:e.id,attrs:{label:e.envName,value:e.id}})}),1),e._v(" "),t("span",{staticClass:"row-span-class span-other"},[e._v("状态")]),e._v(" "),t("el-select",{staticStyle:{width:"10%"},attrs:{clearable:"",placeholder:"请选择",size:"small"},model:{value:e.queryParams.isReviewed,callback:function(a){e.$set(e.queryParams,"isReviewed",a)},expression:"queryParams.isReviewed"}},[t("el-option",{attrs:{value:0,label:"未通过审核"}}),e._v(" "),t("el-option",{attrs:{value:1,label:"通过审核"}})],1)],1),e._v(" "),t("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[t("span",{staticClass:"row-span-class"},[e._v("时间")]),e._v(" "),t("el-date-picker",{attrs:{"end-placeholder":"结束日期","start-placeholder":"开始日期",type:"datetimerange","value-format":"yyyy-MM-dd HH:mm:ss"},model:{value:e.rangeTime,callback:function(a){e.rangeTime=a},expression:"rangeTime"}}),e._v(" "),t("el-input",{staticClass:"input-class",attrs:{clearable:"",placeholder:"模糊查询"},model:{value:e.queryParams.nameCode,callback:function(a){e.$set(e.queryParams,"nameCode",a)},expression:"queryParams.nameCode"}}),e._v(" "),t("el-button",{staticClass:"span-other",attrs:{icon:"el-icon-search",type:"primary"},on:{click:e.getList}},[e._v("查询")])],1),e._v(" "),t("el-row",{staticClass:"filter-container",attrs:{justify:"start",type:"flex"}},[t("el-col",{attrs:{span:12}},[e.$store.getters.authCheck("yx_image_reviewed")?t("el-button",{staticStyle:{float:"left"},attrs:{icon:"el-icon-document-checked",size:"mini",type:"primary"},on:{click:e.openModuleDialog}},[e._v("批量审核\n        ")]):e._e()],1)],1),e._v(" "),t("el-card",[t("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.listLoading,expression:"listLoading"}],ref:"appTable",staticStyle:{width:"100%"},attrs:{data:e.tableData,"highlight-current-row":!0,"element-loading-text":"拼命加载中..."},on:{"selection-change":e.selectChange}},[t("el-table-column",{attrs:{type:"selection",width:"50"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"序号",type:"index",width:"50"},scopedSlots:e._u([{key:"default",fn:function(a){return[e._v("\n            "+e._s(a.$index+1+e.queryParams.pageSize*(e.queryParams.pageNum-1))+"\n          ")]}}])}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"镜像名称",prop:"imageName","show-overflow-tooltip":!0}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"环境code",prop:"envCode"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"中心名称",prop:"centerName"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"创建时间",prop:"createTime"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"审核时间",prop:"reviewTime",width:"180"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"审核状态",prop:"isReviewed"},scopedSlots:e._u([{key:"default",fn:function(a){return[e._v("\n            "+e._s(e._f("isReviewedTxt")(a.row.isReviewed))+"\n          ")]}}])}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"操作",width:"250"},scopedSlots:e._u([{key:"default",fn:function(a){return[t("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"success"},on:{click:function(t){return e.openDialog(a.row,"details")}}},[e._v("详情\n            ")])]}}])})],1)],1),e._v(" "),t("el-dialog",{attrs:{visible:e.eviewedDialog,title:"批量审核",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(a){e.eviewedDialog=a},close:e.closeEviewedDialog}},[t("el-form",{ref:"migrationModule",staticStyle:{width:"100%","max-height":"70vh","overflow-y":"auto","overflow-x":"hidden",padding:"0 5px"},attrs:{model:e.eviewedForm,"label-position":"left","label-width":"50ox"}},[t("el-row",{attrs:{gutter:20}},[t("el-col",[t("el-form-item",{attrs:{label:""}},[t("el-radio-group",{model:{value:e.eviewedForm.isReviewed,callback:function(a){e.$set(e.eviewedForm,"isReviewed",a)},expression:"eviewedForm.isReviewed"}},[t("el-radio",{attrs:{label:0}},[e._v("未通过审核")]),e._v(" "),t("el-radio",{attrs:{label:1}},[e._v("通过审核")])],1)],1)],1)],1)],1),e._v(" "),t("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(a){e.eviewedDialog=!1,e.closeEviewedDialog()}}},[e._v("关闭")]),e._v(" "),t("el-button",{attrs:{type:"primary"},on:{click:e.reviewImage}},[e._v("确定")])],1)],1),e._v(" "),t("el-dialog",{attrs:{visible:e.detailsDialog,title:"详情",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(a){e.detailsDialog=a},close:e.closeDetailsDialog}},[t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("镜像名称：")]),e._v(e._s(e.dialogData.row.imageName)+"\n        ")]),e._v(" "),t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("环境code：")]),e._v(e._s(e.dialogData.row.envCode)+"\n        ")])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("中心名称：")]),e._v(e._s(e.dialogData.row.centerName)+"\n        ")]),e._v(" "),t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("创建时间：")]),e._v(e._s(e.dialogData.row.createTime))])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("审核时间：")]),e._v(e._s(e.dialogData.row.reviewTime)+"\n        ")]),e._v(" "),t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("审核状态：")]),e._v(e._s(e._f("isReviewedTxt")(e.dialogData.row.isReviewed))+"\n        ")])],1)],1)],1),e._v(" "),t("el-footer",{directives:[{name:"show",rawName:"v-show",value:0!=e.tableData.length,expression:"tableData.length != 0"}]},[t("div",{staticClass:"pagination-container"},[t("el-pagination",{attrs:{"current-page":e.queryParams.pageNum,"page-count":e.pages,"page-size":e.queryParams.pageSize,"page-sizes":[5,10,15,30,50],total:e.total,background:"",layout:"total, sizes, prev, pager, next, jumper"},on:{"current-change":e.handleCurrentChange,"size-change":e.handleSizeChange}})],1)])],1)},staticRenderFns:[]};var o=t("VU/8")(s,n,!1,function(e){t("wN1z")},"data-v-745f705b",null);a.default=o.exports},"d6/B":function(e,a,t){"use strict";a.c=function(e){return Object(i.a)({url:"/billing/opoff/show",method:"get",params:e})},a.d=function(e){return Object(i.a)({url:"/billing/opoff/up",method:"post",data:e,transformRequest:[function(e){return e=s.a.stringify(e)}]})},a.b=function(e){return Object(i.a)({url:"/billing/env/show",method:"get",params:e})},a.a=function(e){return Object(i.a)({url:"/billing/op/hostnames",method:"get",params:e})},a.e=function(e){return Object(i.a)({url:"/billing/op/hostnames",method:"post",data:e})};var i=t("Vo7i"),l=t("mw3O"),s=t.n(l)},wN1z:function(e,a){}});