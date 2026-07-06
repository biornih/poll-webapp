import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'

function ParticipatePage() {
    const { id } = useParams()
    const [poll, setPoll] = useState(null)
    const [answers, setAnswers] = useState({})
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        if (!api.isLoggedIn()) {
            navigate('/login')
            return
        }
        loadPoll()
    }, [])

    async function loadPoll() {
        try {
            const pending = await api.getPendingPolls()
            const invitation = pending.find(inv => inv.poll.id === parseInt(id))
            if (!invitation) {
                setError('Poll not found or you are not invited')
                return
            }
            setPoll(invitation.poll)
            const initial = {}
            invitation.poll.questions.forEach(q => {
                initial[q.id] = { questionId: q.id, textValue: null,
                    booleanValue: null, numericValue: null }
            })
            setAnswers(initial)
        } catch (err) {
            setError(err.message)
        }
    }

    function updateAnswer(questionId, field, value) {
        setAnswers(prev => ({
            ...prev,
            [questionId]: { ...prev[questionId], [field]: value }
        }))
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setError(null)
        setLoading(true)
        try {
            await api.submitAnswers(parseInt(id), Object.values(answers))
            navigate('/polls/pending')
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    if (error) {
        return (
            <div>
                <Navbar />
                <div className="container mt-4">
                    <div className="alert alert-danger">{error}</div>
                </div>
            </div>
        )
    }

    if (!poll) {
        return (
            <div>
                <Navbar />
                <div className="container mt-4">
                    <div className="text-center">Loading...</div>
                </div>
            </div>
        )
    }

    return (
        <div>
            <Navbar />
            <div className="container mt-4">
                <div className="row justify-content-center">
                    <div className="col-md-8">
                        <div className="card shadow">
                            <div className="card-body p-4">
                                <h2 className="mb-1">{poll.title}</h2>
                                <p className="text-muted mb-4">{poll.description}</p>
                                {error && <div className="alert alert-danger">{error}</div>}
                                <form onSubmit={handleSubmit}>
                                    {poll.questions.map((q, index) => (
                                        <div key={q.id} className="mb-4">
                                            <label className="form-label fw-bold">
                                                {index + 1}. {q.text}
                                            </label>
                                            {q.type === 'PLAIN_TEXT' && (
                                                <textarea
                                                    className="form-control"
                                                    rows="3"
                                                    onChange={e =>
                                                        updateAnswer(q.id, 'textValue', e.target.value)}
                                                    required
                                                />
                                            )}
                                            {q.type === 'BOOLEAN' && (
                                                <div className="d-flex gap-3 mt-2">
                                                    <div className="form-check">
                                                        <input
                                                            type="radio"
                                                            className="form-check-input"
                                                            name={`q-${q.id}`}
                                                            id={`yes-${q.id}`}
                                                            onChange={() =>
                                                                updateAnswer(q.id, 'booleanValue', true)}
                                                            required
                                                        />
                                                        <label
                                                            className="form-check-label"
                                                            htmlFor={`yes-${q.id}`}
                                                        >
                                                            Yes
                                                        </label>
                                                    </div>
                                                    <div className="form-check">
                                                        <input
                                                            type="radio"
                                                            className="form-check-input"
                                                            name={`q-${q.id}`}
                                                            id={`no-${q.id}`}
                                                            onChange={() =>
                                                                updateAnswer(q.id, 'booleanValue', false)}
                                                        />
                                                        <label
                                                            className="form-check-label"
                                                            htmlFor={`no-${q.id}`}
                                                        >
                                                            No
                                                        </label>
                                                    </div>
                                                </div>
                                            )}
                                            {q.type === 'NUMERIC' && (
                                                <div className="d-flex gap-2 mt-2">
                                                    {[1, 2, 3, 4, 5].map(n => (
                                                        <div key={n} className="form-check">
                                                            <input
                                                                type="radio"
                                                                className="form-check-input"
                                                                name={`q-${q.id}`}
                                                                id={`n-${q.id}-${n}`}
                                                                onChange={() =>
                                                                    updateAnswer(q.id, 'numericValue', n)}
                                                                required
                                                            />
                                                            <label
                                                                className="form-check-label"
                                                                htmlFor={`n-${q.id}-${n}`}
                                                            >
                                                                {n}
                                                            </label>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                    <div className="d-grid">
                                        <button
                                            type="submit"
                                            className="btn btn-purple"
                                            disabled={loading}
                                        >
                                            {loading ? 'Submitting...' : 'Submit Answers'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ParticipatePage