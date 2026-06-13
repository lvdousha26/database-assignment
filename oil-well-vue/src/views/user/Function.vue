<template>
  <div class="city-management-component">
    <!-- 轮播图区域 -->
    <section class="carousel-section">
      <div class="carousel-container">
        <div class="carousel-slide" :class="{ active: currentSlide === 0 }">
          <div class="slide-content">
            <h1 class="slide-title">智能人员重识别系统</h1>
            <p class="slide-subtitle">基于深度学习的先进算法，为城市管理提供精准的人员识别解决方案</p>
          </div>
        </div>
        <div class="carousel-slide" :class="{ active: currentSlide === 1 }">
          <div class="slide-content">
            <h1 class="slide-title">多模态查询技术</h1>
            <p class="slide-subtitle">支持图片、文本等多种查询方式，满足不同场景的识别需求</p>
          </div>
        </div>
        <div class="carousel-slide" :class="{ active: currentSlide === 2 }">
          <div class="slide-content">
            <h1 class="slide-title">全场景应用部署</h1>
            <p class="slide-subtitle">适用于公共安全、交通管理、商业分析等多个城市管理领域</p>
          </div>
        </div>
        <div class="carousel-dots">
          <div class="carousel-dot"
               :class="{ active: currentSlide === 0 }"
               @click="currentSlide = 0"></div>
          <div class="carousel-dot"
               :class="{ active: currentSlide === 1 }"
               @click="currentSlide = 1"></div>
          <div class="carousel-dot"
               :class="{ active: currentSlide === 2 }"
               @click="currentSlide = 2"></div>
        </div>
      </div>
    </section>

    <!-- 功能卡片区域 -->
    <section class="features-section">
      <h2 class="section-title">核心功能模块</h2>
      <div class="features-grid">
        <!-- 单模态查询 -->
        <div class="feature-card available" @click="goToQuery">
          <div class="feature-icon">🔍</div>
          <h3 class="feature-title">单模态查询</h3>
          <p class="feature-description">
            基于图片的人员重识别查询，支持上传目标人员照片，
            在数据库中快速匹配相似人员信息
          </p>
          <span class="feature-status">立即使用</span>
        </div>

        <!-- 图文匹配查询 -->
        <div class="feature-card coming-soon" @click="showComingSoon('图文匹配查询')">
          <div class="feature-icon">🖼️</div>
          <h3 class="feature-title">图文匹配查询</h3>
          <p class="feature-description">
            结合图片和文本描述的多模态查询，
            通过文字描述配合图片提高识别精度
          </p>
          <span class="feature-status">敬请期待</span>
        </div>

        <!-- 全场景应用 -->
        <div class="feature-card coming-soon" @click="showComingSoon('全场景应用')">
          <div class="feature-icon">🌐</div>
          <h3 class="feature-title">全场景应用</h3>
          <p class="feature-description">
            面向不同城市管理场景的定制化应用，
            包括安防监控、交通分析、人流统计等
          </p>
          <span class="feature-status">敬请期待</span>
        </div>
      </div>
    </section>

    <!-- 待开发功能模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-icon">🚧</div>
        <h3 class="modal-title">{{ modalTitle }}</h3>
        <p class="modal-description">
          该功能正在紧张开发中，敬请期待后续版本更新。
          如有疑问，请联系技术支持团队。
        </p>
        <button class="modal-close" @click="closeModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CityManagementComponent',
  data() {
    return {
      // 轮播图状态
      currentSlide: 0,
      slideInterval: null,

      // 模态框状态
      showModal: false,
      modalTitle: ''
    }
  },
  mounted() {
    // 启动轮播图自动播放
    this.startCarousel();
  },
  beforeUnmount() {
    // 清除轮播图定时器
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  },
  methods: {
    // 启动轮播图
    startCarousel() {
      this.slideInterval = setInterval(() => {
        this.currentSlide = (this.currentSlide + 1) % 3;
      }, 5000); // 每5秒切换
    },
    // 跳转到查询页面
    goToQuery() {
      this.$router.push('/func/smr');
    },
    // 显示待开发提示
    showComingSoon(title) {
      this.modalTitle = title;
      this.showModal = true;
    },
    // 关闭模态框
    closeModal() {
      this.showModal = false;
      this.modalTitle = '';
    }
  }
}
</script>

<style scoped>
.city-management-component {
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', sans-serif;
  color: #2c3e50;
  line-height: 1.6;
}

/* === 轮播图样式 === */
.carousel-section {
  margin-bottom: 60px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.carousel-container {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
}

.carousel-slide.active {
  opacity: 1;
}

.carousel-slide:nth-child(1) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.carousel-slide:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.carousel-slide:nth-child(3) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.slide-content {
  text-align: center;
  color: white;
  padding: 40px;
}

.slide-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.slide-subtitle {
  font-size: 18px;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

.carousel-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.carousel-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255,255,255,0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-dot.active {
  background: white;
  transform: scale(1.2);
}

/* === 功能卡片区域 === */
.features-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 50px;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 40px 30px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
  border: 1px solid #e8eaed;
  transition: all 0.3s ease;
  text-align: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: left 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0,0,0,0.12);
}

.feature-card:hover::before {
  left: 0;
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.feature-card.available .feature-icon {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
  box-shadow: 0 4px 16px rgba(5, 150, 105, 0.3);
}

.feature-card.coming-soon .feature-icon {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  box-shadow: 0 4px 16px rgba(107, 114, 128, 0.3);
}

.feature-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.feature-description {
  font-size: 16px;
  color: #6c757d;
  line-height: 1.6;
  margin-bottom: 25px;
}

.feature-status {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  display: inline-block;
}

.feature-card.available .feature-status {
  background: #d1fae5;
  color: #047857;
}

.feature-card.coming-soon .feature-status {
  background: #f3f4f6;
  color: #6b7280;
}

/* === 模态框样式 === */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 40px;
  max-width: 500px;
  width: 90%;
  text-align: center;
  box-shadow: 0 16px 48px rgba(0,0,0,0.2);
}

.modal-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.modal-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.modal-description {
  font-size: 16px;
  color: #6c757d;
  line-height: 1.6;
  margin-bottom: 30px;
}

.modal-close {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
}

.modal-close:hover {
  background: #2563eb;
}

/* === 响应式设计 === */
@media (max-width: 768px) {
  .carousel-container {
    height: 300px;
  }

  .slide-title {
    font-size: 28px;
  }

  .slide-subtitle {
    font-size: 16px;
  }

  .section-title {
    font-size: 26px;
    margin-bottom: 30px;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .feature-card {
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .feature-icon {
    width: 60px;
    height: 60px;
    font-size: 24px;
  }

  .feature-title {
    font-size: 20px;
  }

  .feature-description {
    font-size: 14px;
  }

  .modal-content {
    padding: 30px 20px;
  }
}
</style>