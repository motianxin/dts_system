<template>
  <div class="home-container">
    <h2>欢迎使用DTS问题单系统</h2>
    
    <!-- 测试人员提单功能 -->
    <div v-if="userRole === 'TESTER'" class="create-issue-section">
      <h3>创建问题单</h3>
      <form @submit.prevent="createIssue">
        <div class="form-group">
          <label for="title">标题</label>
          <input type="text" id="title" v-model="newIssue.title" required>
        </div>
        <div class="form-group">
          <label for="description">描述</label>
          <textarea id="description" v-model="newIssue.description" required></textarea>
        </div>
        <div class="form-group">
          <label for="priority">优先级</label>
          <select id="priority" v-model="newIssue.priority" required>
            <option value="LOW">低</option>
            <option value="MEDIUM">中</option>
            <option value="HIGH">高</option>
          </select>
        </div>
        <button type="submit" class="submit-btn">提交工单</button>
      </form>
    </div>
    
    <div class="dashboard">
      <div class="card">
        <h3>待处理问题</h3>
        <p>{{ pendingIssues }}</p>
      </div>
      <div class="card">
        <h3>已解决问题</h3>
        <p>{{ resolvedIssues }}</p>
      </div>
      <div class="card">
        <h3>总问题数</h3>
        <p>{{ totalIssues }}</p>
      </div>
    </div>
    
    <!-- 筛选和导出功能 -->
    <div class="filter-section">
      <h3>筛选问题单</h3>
      <div class="filter-form">
        <div class="filter-group">
          <label for="filterStatus">状态</label>
          <select id="filterStatus" v-model="filterParams.status">
            <option value="">全部</option>
            <option value="OPEN">开放</option>
            <option value="CLOSED">关闭</option>
          </select>
        </div>
        <div class="filter-group">
          <label for="filterPriority">优先级</label>
          <select id="filterPriority" v-model="filterParams.priority">
            <option value="">全部</option>
            <option value="LOW">低</option>
            <option value="MEDIUM">中</option>
            <option value="HIGH">高</option>
          </select>
        </div>
        <div class="filter-group">
          <label for="filterProcessStatus">流程状态</label>
          <select id="filterProcessStatus" v-model="filterParams.processStatus">
            <option value="">全部</option>
            <option value="SUBMITTED">已提交</option>
            <option value="DEVELOPING">开发中</option>
            <option value="DEVELOPMENT_REVIEWING">开发审核中</option>
            <option value="REGRESSING">回归中</option>
            <option value="COMPLETED">已完成</option>
          </select>
        </div>
        <div class="filter-buttons">
          <button @click="applyFilter" class="filter-btn">筛选</button>
          <button @click="resetFilter" class="reset-btn">重置</button>
          <button @click="exportToExcel" class="export-btn">导出Excel</button>
        </div>
      </div>
    </div>
    
    <!-- 问题单列表 -->
    <div class="issues-list">
      <h3>问题单列表</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>状态</th>
            <th>优先级</th>
            <th>流程状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="issue in issues" :key="issue.id">
            <td>{{ issue.id }}</td>
            <td>{{ issue.title }}</td>
            <td>{{ issue.status }}</td>
            <td>{{ issue.priority }}</td>
            <td>{{ issue.processStatus }}</td>
            <td>
              <button v-if="userRole === 'TESTER' && !issue.processStatus" @click="submitIssue(issue.id)" class="action-btn">提交</button>
              <button v-if="userRole === 'TEST_MANAGER' && issue.processStatus === 'SUBMITTED'" @click="reviewIssue(issue.id)" class="action-btn">审核</button>
              <button v-if="userRole === 'DEVELOPER' && issue.processStatus === 'DEVELOPING'" @click="resolveIssue(issue.id)" class="action-btn">处理</button>
              <button v-if="userRole === 'DEV_MANAGER' && issue.processStatus === 'DEVELOPMENT_REVIEWING'" @click="reviewResolution(issue.id)" class="action-btn">审核处理</button>
              <button v-if="userRole === 'TESTER' && issue.processStatus === 'REGRESSING'" @click="completeRegression(issue.id)" class="action-btn">回归</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userRole: 'TESTER',
      newIssue: {
        title: '',
        description: '',
        priority: 'MEDIUM'
      },
      issues: [],
      pendingIssues: 0,
      resolvedIssues: 0,
      totalIssues: 0,
      filterParams: {
        status: '',
        priority: '',
        processStatus: '',
        reporterId: null,
        assigneeId: null
      }
    };
  },
  mounted() {
    this.loadIssues();
  },
  methods: {
    handleLogout() {
      console.log('退出登录');
      this.$router.push('/login');
    },
    async createIssue() {
      try {
        const response = await fetch('/api/issues', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            ...this.newIssue,
            reporterId: 1,
            assigneeId: null
          })
        });
        const result = await response.json();
        if (response.ok) {
          alert('工单创建成功');
          this.newIssue = { title: '', description: '', priority: 'MEDIUM' };
          this.loadIssues();
        } else {
          alert('工单创建失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    },
    async loadIssues() {
      try {
        const response = await fetch('/api/issues');
        const result = await response.json();
        if (response.ok) {
          this.issues = result;
          this.totalIssues = result.length;
          this.pendingIssues = result.filter(issue => issue.status === 'OPEN').length;
          this.resolvedIssues = result.filter(issue => issue.status === 'CLOSED').length;
        }
      } catch (error) {
        console.error('加载问题单失败:', error);
      }
    },
    async applyFilter() {
      try {
        const params = new URLSearchParams();
        if (this.filterParams.status) params.append('status', this.filterParams.status);
        if (this.filterParams.priority) params.append('priority', this.filterParams.priority);
        if (this.filterParams.processStatus) params.append('processStatus', this.filterParams.processStatus);
        if (this.filterParams.reporterId) params.append('reporterId', this.filterParams.reporterId);
        if (this.filterParams.assigneeId) params.append('assigneeId', this.filterParams.assigneeId);
        
        const response = await fetch(`/api/issues/filter?${params.toString()}`);
        const result = await response.json();
        if (response.ok) {
          this.issues = result;
        }
      } catch (error) {
        console.error('筛选问题单失败:', error);
        alert('筛选失败: ' + error.message);
      }
    },
    resetFilter() {
      this.filterParams = {
        status: '',
        priority: '',
        processStatus: '',
        reporterId: null,
        assigneeId: null
      };
      this.loadIssues();
    },
    async exportToExcel() {
      try {
        const params = new URLSearchParams();
        if (this.filterParams.status) params.append('status', this.filterParams.status);
        if (this.filterParams.priority) params.append('priority', this.filterParams.priority);
        if (this.filterParams.processStatus) params.append('processStatus', this.filterParams.processStatus);
        if (this.filterParams.reporterId) params.append('reporterId', this.filterParams.reporterId);
        if (this.filterParams.assigneeId) params.append('assigneeId', this.filterParams.assigneeId);
        
        const response = await fetch(`/api/issues/export?${params.toString()}`);
        if (response.ok) {
          const blob = await response.blob();
          const contentDisposition = response.headers.get('Content-Disposition');
          let fileName = 'issues.xlsx';
          if (contentDisposition) {
            const fileNameMatch = contentDisposition.match(/filename\*=UTF-8''(.+)/);
            if (fileNameMatch && fileNameMatch[1]) {
              fileName = decodeURIComponent(fileNameMatch[1]);
            } else {
              const simpleMatch = contentDisposition.match(/filename="?(.+?)"?(;|$)/);
              if (simpleMatch && simpleMatch[1]) {
                fileName = simpleMatch[1];
              }
            }
          }
          
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = fileName;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
          
          alert('导出成功');
        } else {
          alert('导出失败');
        }
      } catch (error) {
        console.error('导出失败:', error);
        alert('导出失败: ' + error.message);
      }
    },
    async submitIssue(id) {
      try {
        const response = await fetch(`/api/issues/${id}/submit`, {
          method: 'POST'
        });
        if (response.ok) {
          alert('工单提交成功');
          this.loadIssues();
        } else {
          const result = await response.json();
          alert('工单提交失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    },
    reviewIssue(id) {
      const reviewStatus = prompt('请输入审核状态 (APPROVED/REJECTED):');
      const reviewComment = prompt('请输入审核意见:');
      if (reviewStatus && reviewComment) {
        this.doReviewIssue(id, reviewStatus, reviewComment);
      }
    },
    async doReviewIssue(id, reviewStatus, reviewComment) {
      try {
        const response = await fetch(`/api/issues/${id}/review`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `reviewStatus=${reviewStatus}&reviewComment=${encodeURIComponent(reviewComment)}`
        });
        if (response.ok) {
          alert('审核成功');
          this.loadIssues();
        } else {
          const result = await response.json();
          alert('审核失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    },
    resolveIssue(id) {
      const resolution = prompt('请输入处理结果:');
      if (resolution) {
        this.doResolveIssue(id, resolution);
      }
    },
    async doResolveIssue(id, resolution) {
      try {
        const response = await fetch(`/api/issues/${id}/resolve`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `resolution=${encodeURIComponent(resolution)}`
        });
        if (response.ok) {
          alert('处理成功');
          this.loadIssues();
        } else {
          const result = await response.json();
          alert('处理失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    },
    reviewResolution(id) {
      const reviewStatus = prompt('请输入审核状态 (APPROVED/REJECTED):');
      const reviewComment = prompt('请输入审核意见:');
      if (reviewStatus && reviewComment) {
        this.doReviewResolution(id, reviewStatus, reviewComment);
      }
    },
    async doReviewResolution(id, reviewStatus, reviewComment) {
      try {
        const response = await fetch(`/api/issues/${id}/review-resolution`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `reviewStatus=${reviewStatus}&reviewComment=${encodeURIComponent(reviewComment)}`
        });
        if (response.ok) {
          alert('审核处理结果成功');
          this.loadIssues();
        } else {
          const result = await response.json();
          alert('审核处理结果失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    },
    completeRegression(id) {
      const regressionResult = prompt('请输入回归结果:');
      if (regressionResult) {
        this.doCompleteRegression(id, regressionResult);
      }
    },
    async doCompleteRegression(id, regressionResult) {
      try {
        const response = await fetch(`/api/issues/${id}/complete-regression`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: `regressionResult=${encodeURIComponent(regressionResult)}`
        });
        if (response.ok) {
          alert('回归完成');
          this.loadIssues();
        } else {
          const result = await response.json();
          alert('回归失败: ' + result);
        }
      } catch (error) {
        alert('网络错误: ' + error.message);
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  max-width: 1000px;
  margin: 50px auto;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

h3 {
  margin: 20px 0;
  color: #444;
}

.create-issue-section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.form-group textarea {
  height: 100px;
  resize: vertical;
}

.submit-btn {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #45a049;
}

.dashboard {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
}

.card {
  width: 30%;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card h3 {
  margin-bottom: 10px;
  color: #555;
}

.card p {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
}

.filter-section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.filter-group {
  flex: 1;
  min-width: 150px;
}

.filter-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.filter-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn,
.reset-btn,
.export-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.filter-btn {
  background-color: #2196F3;
  color: white;
}

.filter-btn:hover {
  background-color: #0b7dda;
}

.reset-btn {
  background-color: #ff9800;
  color: white;
}

.reset-btn:hover {
  background-color: #e68900;
}

.export-btn {
  background-color: #9c27b0;
  color: white;
}

.export-btn:hover {
  background-color: #7b1fa2;
}

.issues-list {
  margin-bottom: 30px;
}

.issues-list table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.issues-list th,
.issues-list td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.issues-list th {
  background-color: #f2f2f2;
  font-weight: bold;
  color: #333;
}

.issues-list tr:hover {
  background-color: #f5f5f5;
}

.action-btn {
  padding: 5px 10px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  margin-right: 5px;
}

.action-btn:hover {
  background-color: #0b7dda;
}

.logout-btn {
  display: block;
  margin: 0 auto;
  padding: 10px 20px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #d32f2f;
}
</style>
