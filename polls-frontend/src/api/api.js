const BASE_URL = 'http://localhost:8080'

function getAuthHeader(username, password) {
    return 'Basic ' + btoa(username + ':' + password)
}

function getStoredAuthHeader() {
    const username = localStorage.getItem('username')
    const password = localStorage.getItem('password')
    if (!username || !password) return null
    return getAuthHeader(username, password)
}

async function request(method, path, body = null, requiresAuth = true) {
    const headers = { 'Content-Type': 'application/json' }
    if (requiresAuth) {
        const auth = getStoredAuthHeader()
        if (auth) headers['Authorization'] = auth
    }
    const options = { method, headers }
    if (body) options.body = JSON.stringify(body)
    const response = await fetch(BASE_URL + path, options)
    if (!response.ok) {
        const error = await response.json().catch(() => ({}))
        throw { status: response.status, message: error.message || 'An error occurred' }
    }
    if (response.status === 204) return null
    return response.json()
}

export const api = {
    register: (username, password) =>
        request('POST', '/users', { username, password }, false),

    login: async (username, password) => {
        const auth = 'Basic ' + btoa(username + ':' + password)
        const response = await fetch(BASE_URL + '/users/current', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': auth
            }
        })
        if (!response.ok) {
            throw { status: response.status, message: 'Invalid username or password' }
        }
        localStorage.setItem('username', username)
        localStorage.setItem('password', password)
        return response.json()
    },

    getCurrentUser: () => request('GET', '/users/current'),

    createPoll: (data) => request('POST', '/polls', data),
    listPolls: () => request('GET', '/polls'),
    getPoll: (id) => request('GET', `/polls/${id}`),
    updatePoll: (id, data) => request('PUT', `/polls/${id}`, data),
    deletePoll: (id) => request('DELETE', `/polls/${id}`),
    finishPoll: (id) => request('POST', `/polls/${id}/finish`),

    inviteUser: (pollId, username) =>
        request('POST', `/polls/${pollId}/invite`, { username }),
    getPendingPolls: () => request('GET', '/polls/pending'),
    submitAnswers: (pollId, answers) =>
        request('POST', `/polls/${pollId}/participate`, { answers }),

    getIndividualResults: (pollId) =>
        request('GET', `/polls/${pollId}/results/individual`),
    getAggregateResults: (pollId) =>
        request('GET', `/polls/${pollId}/results/aggregate`),

    logout: () => {
        localStorage.removeItem('username')
        localStorage.removeItem('password')
    },

    isLoggedIn: () => !!localStorage.getItem('username'),
}