<template>
  <div>
    <titleCard :title="$t('message.streamis.moduleName.coreIndex')">
      <div class="cardWrap">
        <Card
          v-for="(item, index) in indexItems"
          :key="index"
          style="margin-left: 50px; margin-top: 10px;"
        >
          <div class="cardInner">
            <Icon :type="item.icon" size="26" :color="item.color" />
            <p :style="{ color: item.color, 'font-size': '18px' }">
              {{ item.num }}
            </p>
            <p>{{ $t(`message.streamis.jobStatus.${item.name}`) }}</p>
          </div>
        </Card>
      </div>
    </titleCard>
  </div>
</template>
<script>
import api from "@/common/service/api";
import titleCard from '@/apps/streamis/components/titleCard';
export default {
  components: { titleCard },
  data() {
    return {
      indexItems: [
        { name: "failure", num: 0, icon: "md-close-circle", color: "#990033" },
        { name: "running", num: 0, icon: "md-pint", color: "#008000" },
        { name: "slowTask", num: 0, icon: "md-help-circle", color: "#6666FF" },
        { name: "alert", num: 0, icon: "md-warning", color: "#FF99CC" },
        { name: "waitRestart", num: 0, icon: "md-alert", color: "#FF00CC" },
        {
          name: "success",
          num: 0,
          icon: "md-checkmark-circle",
          color: "#008000"
        }
      ]
    };
  },
  mounted() {
    this.getIndexData();
  },
  methods: {
    getIndexData() {
      api
        .fetch(
          "streamis/streamJobManager/project/core/target",
          "get"
        )
        .then(res => {
          console.log(res);
          const newDatas = [];
          this.indexItems.forEach(item => {
            const newItem = {...item};
            newItem.num = res[`${newItem.name}Num`];
            newDatas.push(newItem)
          });
          this.indexItems= newDatas;
        }).catch(e => console.log(e));
    }
  }
};
</script>
<style lang="scss" scoped>
.cardWrap {
  display: flex;
  flex-wrap: wrap;
}
.cardInner {
  display: flex;
  min-width: 86px;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  & p {
    text-align: center;
  }
}
</style>
