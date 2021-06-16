<template>
  <div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.flinkJarPac") }}</p>
      <div>
        <Table :columns="columns" :data="jarData.mainJars" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
    <div class="itemWrap">
      <p>Program Arguement</p>
      <div class="programArguement">{{jarData.programArguement}}</div>
    </div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.dependJarPac") }}</p>
      <div>
        <Table :columns="columns.filter(item => item.key !== 'entryClass')" :data="jarData.dependentList" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
    <div class="itemWrap">
      <p>{{ $t("message.streamis.jobDetail.userResource") }}</p>
      <div>
        <Table :columns="columns.filter(item => item.key !== 'entryClass')" :data="jarData.userList" border>
          <template slot-scope="{ row }" slot="operation">
            <div>{{ row.id }}</div>
          </template>
        </Table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    jarData: Object
  },
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
    };
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
