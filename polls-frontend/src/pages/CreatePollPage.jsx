import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/api.js'
import Navbar from '../components/Navbar.jsx'

function CreatePollPage() {
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [dueDate, setDueDate] = useState('')
    const [questions, setQuestions] = useState([{ text: '', type: 'PLAIN_TEXT' }])
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    function addQuestion() {
        setQuestions([...questions, { text: '', type: 'PLAIN_TEXT' }])
    }

    function removeQuestion(index) {
        setQuestions(questions.filter((_, i) => i !== index))
    }

    function updateQuestion(index, field, value) {
        setQuestions(questions.map((q, i) =>
            i === index ? { ...q, [field]: value } : q
        ))
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setError(null)
        setLoading(true)
        try {
            await api.createPoll({ title, description, dueDate, questions })
            navigate('/polls')
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <Navbar />
            <div className="container mt-4">
                <div className="row justify-content-center">
                    <div className="col-md-8">
                        <div className="card shadow">
                            <div className="card-body p-4">
                                <h2 className="mb-4">Create New Poll</h2>
                                {error && <div className="alert alert-danger">{error}</div>}
                                <form onSubmit={handleSubmit}>
                                    <div className="mb-3">
                                        <label className="form-label">Title</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={title}
                                            onChange={e => setTitle(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label className="form-label">Description</label>
                                        <textarea
                                            className="form-control"
                                            rows="3"
                                            value={description}
                                            onChange={e => setDescription(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="form-label">Due Date</label>
                                        <input
                                            type="date"
                                            className="form-control"
                                            value={dueDate}
                                            onChange={e => setDueDate(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <h5 className="mb-3">Questions</h5>
                                    {questions.map((q, index) => (
                                        <div key={index} className="card mb-3 bg-light">
                                            <div className="card-body">
                                                <div className="d-flex justify-content-between mb-2">
                                                    <strong>Question {index + 1}</strong>
                                                    {questions.length > 1 && (
                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-danger"
                                                            onClick={() => removeQuestion(index)}
                                                        >
                                                            Remove
                                                        </button>
                                                    )}
                                                </div>
                                                <div className="mb-2">
                                                    <label className="form-label">Question text</label>
                                                    <input
                                                        type="text"
                                                        className="form-control"
                                                        value={q.text}
                                                        onChange={e => updateQuestion(index, 'text', e.target.value)}
                                                        required
                                                    />
                                                </div>
                                                <div>
                                                    <label className="form-label">Answer type</label>
                                                    <select
                                                        className="form-select"
                                                        value={q.type}
                                                        onChange={e => updateQuestion(index, 'type', e.target.value)}
                                                    >
                                                        <option value="PLAIN_TEXT">Plain text</option>
                                                        <option value="BOOLEAN">Yes / No</option>
                                                        <option value="NUMERIC">Numeric (1-5)</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    <button
                                        type="button"
                                        className="btn btn-outline-purple mb-4"
                                        onClick={addQuestion}
                                    >
                                        + Add Question
                                    </button>
                                    <div className="d-grid">
                                        <button
                                            type="submit"
                                            className="btn btn-purple"
                                            disabled={loading}
                                        >
                                            {loading ? 'Creating...' : 'Create Poll'}
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

export default CreatePollPage