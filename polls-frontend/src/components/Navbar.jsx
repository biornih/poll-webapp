import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/api.js'

function Navbar() {
    const [pendingCount, setPendingCount] = useState(0)
    const navigate = useNavigate()

    useEffect(() => {
        if (api.isLoggedIn()) {
            loadPending()
            const interval = setInterval(loadPending, 30000)
            return () => clearInterval(interval)
        }
    }, [])

    async function loadPending() {
        try {
            const data = await api.getPendingPolls()
            setPendingCount(data.length)
        } catch {
            setPendingCount(0)
        }
    }

    function handleLogout() {
        api.logout()
        navigate('/login')
    }

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-purple">
            <div className="container">
                <Link className="navbar-brand fw-bold" to="/polls">
                    PollApp
                </Link>
                <div className="d-flex gap-2 align-items-center">
                    <Link className="btn btn-outline-light btn-sm" to="/polls">
                        My Polls
                    </Link>
                    <Link
                        className={`btn btn-outline-light btn-sm ${pendingCount > 0 ? 'pending-radar' : ''}`}
                        to="/polls/pending"
                    >
                        Pending {pendingCount > 0 && `(${pendingCount})`}
                    </Link>
                    <Link className="btn btn-outline-light btn-sm" to="/polls/create">
                        Create Poll
                    </Link>
                    <Link className="btn btn-outline-light btn-sm" to="/profile">
                        Profile
                    </Link>
                    <button
                        className="btn btn-light btn-sm"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    )
}

export default Navbar