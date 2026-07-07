import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import PollsPage from './pages/PollsPage.jsx'
import CreatePollPage from './pages/CreatePollPage.jsx'
import PendingPollsPage from './pages/PendingPollsPage.jsx'
import ParticipatePage from './pages/ParticipatePage.jsx'
import ResultsPage from './pages/ResultsPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'

function App() {
    return (
        <BrowserRouter>
            <div className="gradient-blob gradient-blob-1"></div>
            <div className="gradient-blob gradient-blob-2"></div>
            <div className="gradient-blob gradient-blob-3"></div>
            <div className="gradient-blob gradient-blob-4"></div>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/polls" element={<PollsPage />} />
                <Route path="/polls/create" element={<CreatePollPage />} />
                <Route path="/polls/pending" element={<PendingPollsPage />} />
                <Route path="/polls/:id/participate" element={<ParticipatePage />} />
                <Route path="/polls/:id/results" element={<ResultsPage />} />
                <Route path="/profile" element={<ProfilePage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App