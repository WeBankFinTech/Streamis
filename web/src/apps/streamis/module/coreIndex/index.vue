<template>
  <div>
    <titleCard :title="$t('message.streamis.moduleName.coreIndex')">
      <div class="cardWrap">
        <Card
          v-for="(item, index) in indexItems"
          :key="index"
          style="margin-left: 50px;"
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
        { name: "failture", num: 0, icon: "md-close-circle", color: "#990033" },
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
          "api/rest_j/v1/streamis/streamJobManager/project/core/target",
          "get"
        )
        .then(res => {
          console.log(res);
        }).catch(e => console.log(e));
    }
  }
};
</script>
<style lang="scss" scoped>
.cardWrap {
  display: flex;
}
.cardInner {
  display: flex;
  width: 86px;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  & p {
    text-align: center;
  }
}
</style>
