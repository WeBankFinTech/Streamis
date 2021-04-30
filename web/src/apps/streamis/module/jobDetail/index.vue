<template>
  <div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.flinkJarPac") }}</p>
      <div>
        <Table :columns="columns" :data="flinkDatas" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
    <div class="itemWrap">
      <p>Program Arguement</p>
      <div class="programArguement">{{programArguement}}</div>
    </div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.dependJarPac") }}</p>
      <div>
        <Table :columns="columns.filter(item => item.key !== 'entryClass')" :data="dependJar" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.userResource") }}</p>
      <div>
        <Table :columns="columns.filter(item => item.key !== 'entryClass')" :data="userResource" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
  </div>
</template>
<script>
import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: "id",
          key: "id"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.name"),
          key: "name"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.version"),
          key: "version"
        },
        {
          title: this.$t(
            "message.streamis.jobDetail.columns.versionDescription"
          ),
          key: "description"
        },
        {
          title: "Entry Class",
          key: "entryClass"
        },
        {
          title: this.$t(
            "message.streamis.jobDetail.columns.versionUploadTime"
          ),
          key: "updateTime"
        },
        {
          title: this.$t("message.streamis.jobDetail.columns.operation"),
          key: "operation"
        }
      ],
      flinkDatas: [],
      dependJar: [],
      userResource: [],
      programArguement: "",
    };
  },
  mounted() {
    console.log(this.$route.params);
    this.getJarDetail();
  },
  methods: {
    getJarDetail() {
      api
        .fetch(
          "streamis/streamJobManager/job/upload/details?jobId=" +
            this.$route.params.id,
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.details) {
            const {mainJars, programArguement, userList, dependentList} = res.details;
            this.flinkDatas = [...mainJars];
            this.dependJar = [...dependentList];
            this.userResource = [...userList];
            this.programArguement = programArguement;

          }
        })
        .catch(e => console.log(e));
    },
    showVersionInfo(row) {
      console.log(row);
    },
    showDetail(row) {
      console.log(row);
    },
    showLogs(row) {
      console.log(row);
    }
  }
};
</script>
<style lang="scss" scoped>
.itemWrap {
  padding: 10px;
  & > p {
    font-weight: 700;
    font-size: 16px;
  }
  & > div {
    margin-left: 20px;
    margin-top: 10px;
  }
}
.programArguement{
  background: rgba(94, 94, 94, 1);
  color: #fff;
  padding: 10px 20px;
  min-height: 64px;;
}
</style>
