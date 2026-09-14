// HTTP Debug JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const testWebBtn = document.getElementById('testWebBtn');
    const testApiBtn = document.getElementById('testApiBtn');
    const testHealthBtn = document.getElementById('testHealthBtn');
    const openDevToolsBtn = document.getElementById('openDevToolsBtn');
    const requestInfo = document.getElementById('requestInfo');
    const responseInfo = document.getElementById('responseInfo');
    const webNameInput = document.getElementById('webName');
    const apiNameInput = document.getElementById('apiName');
    
    // Format timestamp
    function getTimestamp() {
        return new Date().toLocaleString();
    }
    
    // Display request information
    function displayRequestInfo(url, method = 'GET', headers = {}) {
        const timestamp = getTimestamp();
        requestInfo.innerHTML = `
            <div class="mb-2 text-start">
                <strong>Method:</strong> <span class="badge bg-success">${method}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>URL:</strong> <span class="text-primary">${url}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>Timestamp:</strong> <span class="text-muted">${timestamp}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>Headers:</strong>
                <div class="ms-3 text-start">
                    <div class="text-start"><strong>Accept:</strong> application/json, text/plain, */*</div>
                    <div class="text-start"><strong>Content-Type:</strong> application/json</div>
                    <div class="text-start"><strong>User-Agent:</strong> ${navigator.userAgent}</div>
                </div>
            </div>
            <div class="mb-2 text-start">
                <strong>Browser:</strong> <span class="text-info">${navigator.userAgent.split(' ')[0]}</span>
            </div>
        `;
    }
    
    // Display response information
    function displayResponseInfo(status, statusText, data, contentType = 'application/json') {
        const timestamp = getTimestamp();
        const statusClass = status >= 200 && status < 300 ? 'bg-success' : 'bg-danger';
        
        let responseBodyHtml = '';
        if (contentType.includes('application/json')) {
            responseBodyHtml = `<pre class="mb-0 text-start" style="font-size: 0.8em;">${JSON.stringify(data, null, 2)}</pre>`;
        } else if (contentType.includes('text/html')) {
            // Truncate HTML for display
            const truncatedHtml = data.length > 500 ? data.substring(0, 500) + '...' : data;
            responseBodyHtml = `<pre class="mb-0 text-start" style="font-size: 0.7em; max-height: 200px; overflow-y: auto;">${truncatedHtml.replace(/</g, '&lt;').replace(/>/g, '&gt;')}</pre>`;
        } else {
            responseBodyHtml = `<pre class="mb-0 text-start" style="font-size: 0.8em;">${data}</pre>`;
        }
        
        responseInfo.innerHTML = `
            <div class="mb-2 text-start">
                <strong>Status:</strong> <span class="badge ${statusClass}">${status} ${statusText}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>Timestamp:</strong> <span class="text-muted">${timestamp}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>Content-Type:</strong> <span class="text-info">${contentType}</span>
            </div>
            <div class="mb-2 text-start">
                <strong>Response Body:</strong>
                <div class="bg-white p-2 rounded border mt-1 text-start">
                    ${responseBodyHtml}
                </div>
            </div>
        `;
    }
    
    // Test web page
    testWebBtn.addEventListener('click', async function() {
        const name = webNameInput.value.trim();
        const url = name ? `/?name=${encodeURIComponent(name)}` : '/';
        
        displayRequestInfo(url, 'GET');
        
        try {
            const response = await fetch(url);
            const data = await response.text();
            
            displayResponseInfo(response.status, response.statusText, data, 'text/html;charset=UTF-8');
        } catch (error) {
            displayResponseInfo(0, 'Network Error', { error: error.message }, 'application/json');
        }
    });
    
    // Test API endpoint
    testApiBtn.addEventListener('click', async function() {
        const name = apiNameInput.value.trim();
        const url = name ? `/api/hello?name=${encodeURIComponent(name)}` : '/api/hello';
        
        displayRequestInfo(url, 'GET');
        
        try {
            const response = await fetch(url);
            const data = await response.json();
            
            displayResponseInfo(response.status, response.statusText, data);
            
            // Update the main message in the HTML app
            if (response.ok && data.message) {
                updateMainMessage(data.message, name);
            }
        } catch (error) {
            displayResponseInfo(0, 'Network Error', { error: error.message });
        }
    });
    
    // Function to update the main message in the HTML
    function updateMainMessage(message, name) {
        // Update the main greeting message
        const mainMessageElement = document.querySelector('.lead');
        if (mainMessageElement) {
            mainMessageElement.textContent = message;
            mainMessageElement.style.color = '#28a745'; // Green color to indicate update
            mainMessageElement.style.fontWeight = 'bold';
            
            // Add a subtle animation
            mainMessageElement.style.transition = 'all 0.3s ease';
            setTimeout(() => {
                mainMessageElement.style.color = '';
                mainMessageElement.style.fontWeight = '';
            }, 2000);
        }
        
        // Update the web name input to match
        if (name && webNameInput) {
            webNameInput.value = name;
        }
        
        // Show a success notification
        showNotification(`✅ Message updated: "${message}"`, 'success');
    }
    
    // Function to show notifications
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} alert-dismissible fade show`;
        notification.style.position = 'fixed';
        notification.style.top = '20px';
        notification.style.right = '20px';
        notification.style.zIndex = '9999';
        notification.style.minWidth = '300px';
        notification.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-remove after 3 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }
    
    // Test health endpoint
    testHealthBtn.addEventListener('click', async function() {
        const url = '/actuator/health';
        
        displayRequestInfo(url, 'GET');
        
        try {
            const response = await fetch(url);
            const data = await response.json();
            
            displayResponseInfo(response.status, response.statusText, data);
        } catch (error) {
            displayResponseInfo(0, 'Network Error', { error: error.message });
        }
    });
    
    // Show developer tools instructions
    openDevToolsBtn.addEventListener('click', function() {
        alert(`How to Use Browser Developer Tools:

🔧 OPENING DEV TOOLS:
1. Press F12 (most browsers)
2. Or right-click → "Inspect" → "Developer Tools"
3. Or Ctrl+Shift+I (Windows/Linux) / Cmd+Option+I (Mac)

🌐 NETWORK TAB:
1. Click the "Network" tab
2. Click the test buttons above
3. Watch HTTP requests appear in real-time!

🔄 SPRING BOOT DEVTOOLS LIVE RELOAD:
This app uses Spring Boot DevTools for automatic reloading:
- Edit any Kotlin/Java file → App restarts automatically
- Edit HTML/CSS/JS files → Browser refreshes automatically
- No manual restart needed during development!

🔍 WHAT YOU'LL SEE:
- Request headers and details
- Response data and timing
- Status codes and errors
- Request/response sizes
- Automatic page refresh on file changes

This is how professional Spring Boot developers work!`);
    });
    
    // Check for Spring Boot DevTools LiveReload
    function checkLiveReload() {
        const isDevelopment = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
        
        if (isDevelopment) {
            // Add DevTools indicator
            const devToolsIndicator = document.createElement('div');
            devToolsIndicator.innerHTML = `
                <div style="position: fixed; bottom: 10px; right: 10px; background: #007bff; color: white; padding: 5px 10px; border-radius: 5px; font-size: 12px; z-index: 9999;">
                    🚀 Spring Boot DevTools Active
                </div>
            `;
            document.body.appendChild(devToolsIndicator);
            
            // Setup LiveReload monitoring
            setupLiveReloadMonitoring();
            
            console.log('🚀 Spring Boot DevTools detected!');
            console.log('💡 Edit any file and watch the magic happen - automatic reload!');
        }
    }

    // Setup LiveReload monitoring and reload warnings
    function setupLiveReloadMonitoring() {
        let isReloading = false;
        let reloadWarningShown = false;
        
        // Create reload warning overlay
        const reloadOverlay = document.createElement('div');
        reloadOverlay.id = 'reload-warning-overlay';
        reloadOverlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            color: white;
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 10000;
            font-family: Arial, sans-serif;
        `;
        
        reloadOverlay.innerHTML = `
            <div style="text-align: center; padding: 20px; background: #333; border-radius: 10px; max-width: 400px;">
                <div style="font-size: 48px; margin-bottom: 20px;">🔄</div>
                <h3 style="margin: 0 0 10px 0; color: #ffc107;">Application Reloading</h3>
                <p style="margin: 0; color: #ccc;">Spring Boot DevTools is restarting the application...</p>
                <div style="margin-top: 20px;">
                    <div style="width: 100%; height: 4px; background: #555; border-radius: 2px; overflow: hidden;">
                        <div id="reload-progress" style="width: 0%; height: 100%; background: #28a745; transition: width 0.3s ease;"></div>
                    </div>
                </div>
                <div style="margin-top: 20px;">
                    <button id="manual-reload-btn" onclick="window.location.reload(true)" style="
                        background: #007bff; 
                        color: white; 
                        border: none; 
                        padding: 8px 16px; 
                        border-radius: 4px; 
                        cursor: pointer;
                        font-size: 14px;
                    ">Manual Reload</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(reloadOverlay);
        
        // Monitor for connection loss (indicates restart)
        function checkConnection() {
            fetch('/actuator/health')
                .then(response => {
                    if (response.ok && isReloading) {
                        // Connection restored - app has restarted
                        console.log('✅ Application is back online - reloading page');
                        hideReloadWarning();
                        
                        // Try multiple reload methods to ensure it works
                        setTimeout(() => {
                            console.log('🔄 Attempting page reload...');
                            
                            // Method 1: Force reload with cache bypass
                            try {
                                window.location.reload(true);
                            } catch (e) {
                                console.log('⚠️ Method 1 failed, trying method 2...');
                                
                                // Method 2: Replace current page
                                try {
                                    window.location.replace(window.location.href);
                                } catch (e2) {
                                    console.log('⚠️ Method 2 failed, trying method 3...');
                                    
                                    // Method 3: Assign href
                                    window.location.href = window.location.href;
                                }
                            }
                        }, 1500);
                        
                        // Backup method: Force reload after additional delay
                        setTimeout(() => {
                            if (isReloading) {
                                console.log('🔄 Backup reload method triggered');
                                window.location.reload(true);
                            }
                        }, 3000);
                    }
                })
                .catch(error => {
                    if (!isReloading && !reloadWarningShown) {
                        // Connection lost - app is restarting
                        console.log('❌ Connection lost - app is restarting');
                        showReloadWarning();
                    }
                });
        }
        
        function showReloadWarning() {
            isReloading = true;
            reloadWarningShown = true;
            reloadOverlay.style.display = 'flex';
            
            // Animate progress bar
            const progressBar = document.getElementById('reload-progress');
            let progress = 0;
            const interval = setInterval(() => {
                progress += Math.random() * 15;
                if (progress > 90) progress = 90;
                progressBar.style.width = progress + '%';
            }, 200);
            
            // Store interval for cleanup
            reloadOverlay.dataset.interval = interval;
            
            // Set a timeout to force reload if automatic detection fails
            const timeoutId = setTimeout(() => {
                if (isReloading) {
                    console.log('⏰ Timeout reached - forcing page reload');
                    window.location.reload(true);
                }
            }, 10000); // 10 second timeout
            
            // Store timeout for cleanup
            reloadOverlay.dataset.timeout = timeoutId;
            
            console.log('🔄 Application restart detected - showing reload warning');
        }
        
        function hideReloadWarning() {
            isReloading = false;
            reloadWarningShown = false;
            reloadOverlay.style.display = 'none';
            
            // Clear progress animation
            const interval = reloadOverlay.dataset.interval;
            if (interval) {
                clearInterval(interval);
                delete reloadOverlay.dataset.interval;
            }
            
            // Clear timeout
            const timeout = reloadOverlay.dataset.timeout;
            if (timeout) {
                clearTimeout(timeout);
                delete reloadOverlay.dataset.timeout;
            }
            
            console.log('✅ Application restart complete - hiding reload warning');
        }
        
        // Try to connect to LiveReload WebSocket for better detection
        function connectToLiveReload() {
            try {
                const ws = new WebSocket('ws://localhost:35729/livereload');
                
                ws.onopen = function() {
                    console.log('🔗 Connected to LiveReload WebSocket');
                    
                    // If we were reloading and now we're connected, trigger reload
                    if (isReloading) {
                        console.log('🔄 WebSocket reconnected during reload - triggering page reload');
                        setTimeout(() => {
                            window.location.reload(true);
                        }, 500);
                    }
                };
                
                ws.onclose = function() {
                    console.log('🔌 LiveReload WebSocket disconnected');
                    if (!isReloading) {
                        showReloadWarning();
                    }
                    // Try to reconnect after a delay
                    setTimeout(() => {
                        if (!isReloading) {
                            console.log('🔄 Attempting to reconnect to LiveReload WebSocket');
                            connectToLiveReload();
                        }
                    }, 3000);
                };
                
                ws.onerror = function(error) {
                    console.log('❌ LiveReload WebSocket error:', error);
                };
                
                ws.onmessage = function(event) {
                    try {
                        const data = JSON.parse(event.data);
                        if (data.command === 'reload') {
                            console.log('🔄 LiveReload command received');
                            showReloadWarning();
                        }
                    } catch (e) {
                        console.log('⚠️ Could not parse LiveReload message:', event.data);
                    }
                };
                
                return ws;
            } catch (error) {
                console.log('⚠️ Could not connect to LiveReload WebSocket, using fallback method');
                return null;
            }
        }
        
        // Connect to LiveReload WebSocket
        const liveReloadWs = connectToLiveReload();
        
        // Fallback: Check connection every 1 second for faster detection
        setInterval(checkConnection, 1000);
        
        // Additional method: Check if page content has changed
        let lastPageContent = '';
        setInterval(() => {
            if (isReloading) {
                fetch('/')
                    .then(response => response.text())
                    .then(html => {
                        if (html !== lastPageContent && lastPageContent !== '') {
                            console.log('🔄 Page content changed - triggering reload');
                            window.location.reload(true);
                        }
                        lastPageContent = html;
                    })
                    .catch(() => {
                        // Ignore errors during restart
                    });
            } else {
                // Update last known content when not reloading
                fetch('/')
                    .then(response => response.text())
                    .then(html => {
                        lastPageContent = html;
                    })
                    .catch(() => {
                        // Ignore errors
                    });
            }
        }, 2000);
        
        // Additional check: Monitor for page visibility changes
        let lastCheckTime = Date.now();
        setInterval(() => {
            const now = Date.now();
            if (now - lastCheckTime > 5000) { // If no check for 5 seconds, force a check
                checkConnection();
            }
            lastCheckTime = now;
        }, 500);
        
        // Also listen for beforeunload event as backup
        window.addEventListener('beforeunload', function() {
            if (!isReloading) {
                showReloadWarning();
            }
        });
        
        // Listen for page visibility changes
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible' && isReloading) {
                // Page became visible again, check if app is back
                setTimeout(checkConnection, 1000);
            }
        });
        
        // Cleanup WebSocket on page unload
        window.addEventListener('beforeunload', function() {
            if (liveReloadWs) {
                liveReloadWs.close();
            }
        });
    }
    
    // Initialize DevTools check
    checkLiveReload();
    
    // Sync input fields
    function syncInputFields() {
        if (webNameInput && apiNameInput) {
            webNameInput.addEventListener('input', function() {
                apiNameInput.value = this.value;
            });
            
            apiNameInput.addEventListener('input', function() {
                webNameInput.value = this.value;
            });
        }
    }
    
    // Initialize input synchronization
    syncInputFields();
    
    // Add some educational console messages
    console.log('🌐 HTTP Debug Tool Loaded!');
    console.log('💡 Tip: Open Developer Tools (F12) and check the Network tab to see all HTTP requests');
    console.log('📚 This demonstrates the Fetch API - the modern way to make HTTP requests in JavaScript');
    console.log('🔄 API responses now update the main page message dynamically!');
});
