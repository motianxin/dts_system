<template>
  <div class="login-container">
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" id="username" v-model="form.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="form.password" required>
      </div>
      <button type="submit">登录</button>
      <div class="divider">或</div>
      <button @click="handleFeishuLogin" class="feishu-btn">
        <span class="feishu-icon">飞书</span>
        使用飞书扫码登录
      </button>
      <p class="register-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    handleLogin() {
      // 这里可以添加登录逻辑
      console.log('登录信息:', this.form)
      // 模拟登录成功，跳转到首页
      this.$router.push('/home')
    },
    async handleFeishuLogin() {
      try {
        // 获取飞书登录URL
        const response = await fetch('/api/auth/feishu/login')
        const data = await response.json()
        
        if (data.loginUrl) {
          // 打开飞书登录页面
          window.open(data.loginUrl, '_blank', 'width=600,height=400')
        }
      } catch (error) {
        console.error('获取飞书登录URL失败:', error)
        alert('获取飞书登录链接失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

button:hover {
  background-color: #45a049;
}

.register-link {
  text-align: center;
  margin-top: 15px;
}

.register-link a {
  color: #4CAF50;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.divider {
  text-align: center;
  margin: 15px 0;
  color: #999;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 45%;
  height: 1px;
  background-color: #ddd;
}

.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  right: 0;
  width: 45%;
  height: 1px;
  background-color: #ddd;
}

.feishu-btn {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feishu-btn:hover {
  background-color: #0069d9;
}

.feishu-icon {
  margin-right: 8px;
  font-weight: bold;
}
</style>