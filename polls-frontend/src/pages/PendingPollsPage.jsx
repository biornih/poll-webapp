import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'

function PendingPollsPage() {
    const [invitations, setInvitations] = useState([])
    const [error, setError] = useState(null)
    const navigate = useNavigate()

    useEffect(() => {
        if (!api.isLoggedIn()) {
            navigate('/login')
            return
        }
        loadPending()
    }, [])

    async function loadPending() {
        try {
            const data = await api.getPendingPolls()
            setInvitations(data)
        } catch (err) {
            setError(err.message)
        }
    }

    return (
        <div>
            <Navbar />
            <div className="container mt-4">
                <h2 className="mb-4">Pending Polls</h2>
                {error && <div className="alert alert-danger">{error}</div>}
                {invitations.length === 0 && (
                    <div className="alert alert-info">
                        No pending polls — you are all caught up!
                    </div>
                )}
                {invitations
                    .sort((a, b) => a.poll.dueDate.localeCompare(b.poll.dueDate))
                    .map(inv => (
                        <div key={inv.id} className="card mb-3 shadow-sm">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h5 className="card-title">{inv.poll.title}</h5>
                                        <p className="card-text text-muted mb-1">
                                            {inv.poll.description}
                                        </p>
                                        <small className="text-muted">
                                            Due: {new Date(inv.poll.dueDate).toLocaleDateString('de-DE')}                                            {inv.poll.questions.length} questions &nbsp;|&nbsp;
                                            Created by: {inv.poll.creator.username}
                                        </small>
                                    </div>
                                    <Link
                                        to={`/polls/${inv.poll.id}/participate`}
                                        className="btn btn-purple"
                                    >
                                        Answer
                                    </Link>
                                </div>
                            </div>
                        </div>
                    ))}
            </div>
        </div>
    )
}

export default PendingPollsPage